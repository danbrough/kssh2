package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.lib.LibSSH2.Session.waitSocket
import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.LIBSSH2_SESSION
import io.github.danbrough.libssh2.cinterop.SSH_DISCONNECT_BY_APPLICATION
import io.github.danbrough.libssh2.cinterop.kssh2_exit
import io.github.danbrough.libssh2.cinterop.kssh2_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_connect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_disconnect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_free
import io.github.danbrough.libssh2.cinterop.libssh2_agent_get_identity
import io.github.danbrough.libssh2.cinterop.libssh2_agent_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_list_identities
import io.github.danbrough.libssh2.cinterop.libssh2_agent_publickey
import io.github.danbrough.libssh2.cinterop.libssh2_agent_userauth
import io.github.danbrough.libssh2.cinterop.libssh2_channel_close
import io.github.danbrough.libssh2.cinterop.libssh2_channel_process_startup
import io.github.danbrough.libssh2.cinterop.libssh2_channel_write_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_disconnect_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_free
import io.github.danbrough.libssh2.cinterop.libssh2_session_handshake
import io.github.danbrough.libssh2.cinterop.libssh2_session_init_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_error
import io.github.danbrough.libssh2.cinterop.libssh2_session_set_blocking
import io.github.danbrough.libssh2.cinterop.libssh2_userauth_password_ex
import io.github.danbrough.libssh2.cinterop.ssh2_socket_close
import io.github.danbrough.libssh2.cinterop.ssh2_socket_connect
import io.github.danbrough.libssh2.cinterop.waitsocket
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


actual object LibSSH2 {

  actual fun testJNI(){}

  actual fun initLib() {
    kssh2_init(0).also {
      logNative.debug { "LibSSH2Native::initLib() kssh2_init() returned: $it" }
    }

  }

  actual fun closeLib() {
    logNative.trace { "LibSSH2Native::closeLib() calling kssh2_exit()" }
    kssh2_exit()
  }

  actual object Socket {
    actual fun connect(hostName: String, port: Int): SocketHandle =
      ssh2_socket_connect(hostName, port).convert()

    actual fun close(socket: SocketHandle) {
      if (socket != 0L)
        ssh2_socket_close(socket.toInt())
    }
  }


  actual object Session {
    actual fun createSession(blocking: Boolean): SessionPtr {
      logNative.trace { "LibSSH2Native::Session::createSession(blocking=$blocking)" }
      val session: CPointer<LIBSSH2_SESSION> =
        libssh2_session_init_ex(null, null, null, null)
          ?: return 0

      libssh2_session_set_blocking(session, if (blocking) 1 else 0)
      return session.toLong()
    }

    actual fun close(session: SessionPtr) {
      session.toCPointer<LIBSSH2_SESSION>()?.also { sessionPtr ->
        logNative.trace { "LibSSH2Native::Session::closeSession()" }
        libssh2_session_disconnect_ex(
          sessionPtr,
          SSH_DISCONNECT_BY_APPLICATION,
          "Normal Shutdown",
          ""
        )
        libssh2_session_free(sessionPtr)
      }
    }

    actual fun sessionHandshake(session: SessionPtr, socket: SocketHandle): Int {
      logNative.trace { "LibSSH2Native::Session::sessionHandshake()" }
      var rc: Int
      do {
        rc = libssh2_session_handshake(session.toCPointer(), socket.toInt())
      } while (rc == LIBSSH2_ERROR_EAGAIN)
      if (rc != 0) logNative.error { "libssh2_session_handshake(session, sock) failed. returned: $rc" }
      return rc
    }

    actual fun waitSocket(session: SessionPtr, socket: SocketHandle): Long =
      waitsocket(socket.convert(), session.toCPointer()).toLong()


    actual fun authenticateWithAgent(
      session: SessionPtr,
      socket: SocketHandle,
      remoteUser: String
    ): AgentPtr {

      val agent: CPointer<cnames.structs._LIBSSH2_AGENT> =
        libssh2_agent_init(session.toCPointer())
          ?: error("authenticateWithAgent::libssh2_agent_init() failed")
      logNative.trace { "authenticateWithAgent::libssh2_agent_init() success agent: $agent" }

      runCatching {

        libssh2_agent_connect(agent).takeIf { it != 0 }?.also {
          error("authenticateWithAgent::libssh2_agent_connect() failed. error:$it")
        }
        logNative.trace { "authenticateWithAgent::libssh2_agent_connect() success" }


        libssh2_agent_list_identities(agent).takeIf { it != 0 }?.also {
          error("libssh2_agent_list_identities failed error: $it")
        }
        logNative.trace { "authenticateWithAgent::libssh2_agent_list_identities success" }

        memScoped {
          val identityVar = alloc<CPointerVar<libssh2_agent_publickey>>()
          identityVar.value = null

          var prev: CPointer<libssh2_agent_publickey>? = null
          var success = false

          while (libssh2_agent_get_identity(agent, identityVar.ptr, prev) == 0) {
            logNative.trace { "trying to authenticate with identity: ${identityVar.pointed?.comment?.toKString()}" }
            libssh2_agent_userauth(agent, remoteUser, identityVar.value).also {
              if (it == 0) {
                println("Authentication successful!")
                success = true
                break
              } else {
                logNative.trace { "Failed err: $it  LIBSSH2_ERROR_EAGAIN = $LIBSSH2_ERROR_EAGAIN" }
                if (it == LIBSSH2_ERROR_EAGAIN) {
                  waitSocket(session, socket)
                  continue
                }
              }
            }
            prev = identityVar.value
          }

          logNative.info { "authenticateWithAgent::finished. success: $success" }
        }
      }.exceptionOrNull()?.also {
        Agent.close(agent.toLong())
        throw it
      }
      return agent.toLong()
    }


    /*
int
libssh2_userauth_publickey_frommemory(LIBSSH2_SESSION *session,
                                    const char *username,
                                    size_t username_len,
                                    const char *publickeydata,
                                    size_t publickeydata_len,
                                    const char *privatekeydata,
                                    size_t privatekeydata_len,
                                    const char *passphrase);
   */

    actual fun authenticatePublicKey(
      sessionPtr: SessionPtr,
      socket: SocketHandle,
      user: String?,
      publicKeyData: String?,
      privateKeyData: String?,
      passphrase: String?
    ): Int = nativeSessionAuthenticatePublicKey(
      sessionPtr,
      socket,
      user,
      publicKeyData,
      privateKeyData,
      passphrase
    )

    actual fun authenticatePassword(
      sessionPtr: SessionPtr,
      socket: SocketHandle,
      userName: String,
      password: String
    ): Int {
      var ret = 0
      while (true) {
        ret = libssh2_userauth_password_ex(
          sessionPtr.toCPointer(),
          userName,
          userName.length.toUInt(),
          password,
          password.length.toUInt(),
          null
        )
        if (ret == LIBSSH2_ERROR_EAGAIN) waitSocket(sessionPtr, socket)
        else if (ret <= 0) break
      }
      return ret
    }

    actual fun getError(session: SessionPtr): String =
      memScoped {
        val errMessageVar = alloc<CPointerVar<ByteVar>>()
        val errMessageLenVar = alloc<IntVar>()

        libssh2_session_last_error(
          session.toCPointer(),
          errMessageVar.ptr,
          errMessageLenVar.ptr,
          0
        )

        val nativeMessage: CPointer<ByteVar>? = errMessageVar.value
        return nativeMessage?.toKString() ?: "Unknown error"
      }
  }


  actual object Agent {
    actual fun close(agent: AgentPtr) {
      if (agent != 0L) {
        libssh2_agent_disconnect(agent.toCPointer());
        libssh2_agent_free(agent.toCPointer());
      }
    }
  }

  actual object Channel {

    actual fun channelOpen(
      session: SessionPtr,
      socket: SocketHandle,
      channelType: String,
      windowSize: Int,
      packetSize: Int,
      message: String?
    ): ChannelPtr = nativeChannelOpen(session, socket, channelType, windowSize, packetSize, message)

    /*
    LIBSSH2_API int libssh2_channel_request_pty_ex(LIBSSH2_CHANNEL *channel,
                                               const char *term,
                                               unsigned int term_len,
                                               const char *modes,
                                               unsigned int modes_len,
                                               int width, int height,
                                               int width_px, int height_px);
    #define libssh2_channel_request_pty(channel, term)                      \
    libssh2_channel_request_pty_ex((channel), (term),                   \
                                   (unsigned int)strlen(term),          \
                                   NULL, 0,                             \
                                   LIBSSH2_TERM_WIDTH,                  \
                                   LIBSSH2_TERM_HEIGHT,                 \
                                   LIBSSH2_TERM_WIDTH_PX,               \
                                   LIBSSH2_TERM_HEIGHT_PX)

     */
    actual fun requestPty(channelPtr: ChannelPtr, terminal: String): Long =
      nativeSessionRequestPty(channelPtr, terminal)

    /*
    #define libssh2_channel_exec(channel, command) \
      libssh2_channel_process_startup((channel), "exec", sizeof("exec") - 1, \
      (command), (unsigned int)strlen(command))


      #include <libssh2.h>

int
libssh2_channel_process_startup(LIBSSH2_CHANNEL *channel,
                                const char *request,
                                unsigned int request_len,
                                const char *message,
                                unsigned int message_len);
Description
channel - Active session channel instance.

request - Type of process to startup. The SSH2 protocol currently defines shell, exec, and subsystem as standard process services.

request_len - Length of request parameter.

message - Request specific message data to include.

message_len - Length of message parameter.

Initiate a request on a session type channel such as returned by libssh2_channel_open_ex(3).

Return value
Return 0 on success or negative on failure. It returns LIBSSH2_ERROR_EAGAIN when it would otherwise block. While LIBSSH2_ERROR_EAGAIN is a negative number, it is not really a failure per se.

Errors
LIBSSH2_ERROR_ALLOC - An internal memory allocation call failed.

LIBSSH2_ERROR_SOCKET_SEND - Unable to send data on socket.

LIBSSH2_ERROR_CHANNEL_REQUEST_DENIED -
     */

    actual fun processStartup(
      sessionPtr: SessionPtr,
      socketHandle: SocketHandle,
      channel: ChannelPtr,
      request: String,
      message: String
    ): Long {
      while (true) {
        val ret = libssh2_channel_process_startup(
          channel.toCPointer(),
          request,
          request.length.convert(),
          message,
          message.length.convert()
        )
        if (ret == LIBSSH2_ERROR_EAGAIN) {
          waitSocket(sessionPtr, socketHandle)
          continue
        }
        return ret.toLong()
      }
    }


    actual fun close(channel: ChannelPtr) {
      logNative.trace { "LibSSH2Native::Channel::close()" }
      if (channel != 0L)
        libssh2_channel_close(channel.toCPointer())
    }

    actual fun write(channel: ChannelPtr, data: String): Long =
      libssh2_channel_write_ex(channel.toCPointer(), 0, data, data.length.convert())

    actual suspend fun readAll(channelPtr: ChannelPtr): Flow<ByteArray> = flow {
      val sshChannel = SshChannelNative(channelPtr.toCPointer()!!)
      val buf = ByteArray(1024)
      while (true) {
        val read = sshChannel.read(0, buf)
        if (read <= 0) break
        emit(buf.copyOfRange(0, read))
      }
    }

    actual fun read(
      session: SessionPtr,
      socketHandle: SocketHandle,
      channelPtr: ChannelPtr,
      streamId: Int,
      buffer: ByteArray
    ): Long = nativeSessionRead(session, socketHandle, channelPtr, streamId, buffer)
  }
}

