package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.LibSSH2.Session.waitSocket
import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.LIBSSH2_SESSION
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_HEIGHT
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_HEIGHT_PX
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_WIDTH
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_WIDTH_PX
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
import io.github.danbrough.libssh2.cinterop.libssh2_channel_open_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_process_startup
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_request_pty_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_write_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_disconnect_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_free
import io.github.danbrough.libssh2.cinterop.libssh2_session_handshake
import io.github.danbrough.libssh2.cinterop.libssh2_session_init_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_errno
import io.github.danbrough.libssh2.cinterop.libssh2_session_set_blocking
import io.github.danbrough.libssh2.cinterop.ssh2_socket_close
import io.github.danbrough.libssh2.cinterop.ssh2_socket_connect
import io.github.danbrough.libssh2.cinterop.waitsocket
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.posix.sleep

private val log = logNative

actual object LibSSH2 {
  actual fun initLib() {
    kssh2_init(0).also {
      log.debug { "LibSSH2Native::initLib() kssh2_init() returned: $it" }
    }
  }

  actual fun closeLib() {
    log.trace { "LibSSH2Native::closeLib() calling kssh2_exit()" }
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
      log.trace { "LibSSH2Native::Session::createSession(blocking=$blocking)" }
      val session: CPointer<LIBSSH2_SESSION> =
        libssh2_session_init_ex(null, null, null, null)
          ?: error("Failed to created ssh session")

      libssh2_session_set_blocking(session, if (blocking) 1 else 0)
      return session.toLong()
    }

    actual fun close(session: SessionPtr) {
      session.toCPointer<LIBSSH2_SESSION>()?.also { sessionPtr ->
        log.trace { "LibSSH2Native::Session::closeSession()" }
        libssh2_session_disconnect_ex(
          sessionPtr,
          SSH_DISCONNECT_BY_APPLICATION,
          "Normal Shutdown",
          ""
        )
        libssh2_session_free(sessionPtr)
      }
    }

    actual fun sessionHandshake(session: SessionPtr, socket: SocketHandle): Long {
      log.trace { "LibSSH2Native::Session::sessionHandshake()" }
      var rc: Int
      do {
        rc = libssh2_session_handshake(session.toCPointer(), socket.toInt())
      } while (rc == LIBSSH2_ERROR_EAGAIN)
      if (rc != 0) error("libssh2_session_handshake(session, sock) failed. returned: $rc")
      return rc.convert()
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
      log.trace { "authenticateWithAgent::libssh2_agent_init() success agent: $agent" }

      runCatching {

        libssh2_agent_connect(agent).takeIf { it != 0 }?.also {
          error("authenticateWithAgent::libssh2_agent_connect() failed. error:$it")
        }
        log.trace { "authenticateWithAgent::libssh2_agent_connect() success" }


        libssh2_agent_list_identities(agent).takeIf { it != 0 }?.also {
          error("libssh2_agent_list_identities failed error: $it")
        }
        log.trace { "authenticateWithAgent::libssh2_agent_list_identities success" }

        memScoped {
          val identityVar = alloc<CPointerVar<libssh2_agent_publickey>>()
          identityVar.value = null

          var prev: CPointer<libssh2_agent_publickey>? = null
          var success = false

          while (libssh2_agent_get_identity(agent, identityVar.ptr, prev) == 0) {
            log.trace { "trying to authenticate with identity: ${identityVar.pointed?.comment?.toKString()}" }
            libssh2_agent_userauth(agent, remoteUser, identityVar.value).also {
              if (it == 0) {
                println("Authentication successful!")
                success = true
                break
              } else {
                log.trace { "Failed err: $it  LIBSSH2_ERROR_EAGAIN = $LIBSSH2_ERROR_EAGAIN" }
                if (it == LIBSSH2_ERROR_EAGAIN) {
                  waitSocket(session, socket)
                  continue
                }
              }
            }
            prev = identityVar.value
          }

          log.info { "authenticateWithAgent::finished. success: $success" }
        }
      }.exceptionOrNull()?.also {
        Agent.close(agent.toLong())
        throw it
      }
      return agent.toLong()
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
    /*
#define libssh2_channel_open_session(session) \
libssh2_channel_open_ex((session), "session", sizeof("session") - 1, \
                        LIBSSH2_CHANNEL_WINDOW_DEFAULT, \
                        LIBSSH2_CHANNEL_PACKET_DEFAULT, NULL, 0)
#include <libssh2.h>

LIBSSH2_CHANNEL *
libssh2_channel_open_ex(LIBSSH2_SESSION *session, const char *channel_type,
                        unsigned int channel_type_len,
                        unsigned int window_size,
                        unsigned int packet_size,
                        const char *message, unsigned int message_len);

LIBSSH2_CHANNEL *
libssh2_channel_open_session(session);
*/
    actual fun channelOpen(
      session: SessionPtr,
      socket: SocketHandle,
      channelType: String,
      windowSize: Int,
      packetSize: Int,
      message: String?
    ): ChannelPtr {

      logNative.debug { "channelOpen() session:$session socket:$socket channel type:$channelType windowSize:$windowSize packetSize:$packetSize" }

      var channel: CPointer<LIBSSH2_CHANNEL>? = null
      var rc = LIBSSH2_ERROR_EAGAIN
      while (true) {
        channel = libssh2_channel_open_ex(
          session.toCPointer(),
          channelType,
          channelType.length.convert(),
          windowSize.convert(),
          packetSize.convert(),
          message,
          message?.length?.convert() ?: 0u
        )

        if (channel != null) break
        log.trace { "channelOpen() libssh2_channel_open_ex() returned" }
        rc = libssh2_session_last_errno(session.toCPointer())
        log.trace { "channelOpen() libssh2_channel_open_ex() rc = $rc" }
        if (rc != LIBSSH2_ERROR_EAGAIN) break
        waitSocket(session, socket)
      }

      if (channel == null) error("libssh2_channel_open_ex(channelType=$channelType) -> $rc")
      return channel.toLong()
    }

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
    actual fun requestPty(channelPtr: ChannelPtr, terminal: String): Long {
      log.trace { "LibSSH2Native::Channel::requestPty() terminal:$terminal" }
      var rc = 0
      while (true) {
        rc = libssh2_channel_request_pty_ex(
          channelPtr.toCPointer(),
          terminal,
          terminal.length.convert(),
          null,
          0u,
          LIBSSH2_TERM_WIDTH.convert(),
          LIBSSH2_TERM_HEIGHT.convert(),
          LIBSSH2_TERM_WIDTH_PX.convert(),
          LIBSSH2_TERM_HEIGHT_PX.convert()
        )
        if (rc == LIBSSH2_ERROR_EAGAIN) {
          //TODO delay(10.milliseconds)
          continue
        }
        return rc.toLong()
      }
    }

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

    fun exec(channel: ChannelPtr, cmdLine: String): Long {
      return 0L
    }


    actual fun close(channel: ChannelPtr) {
      log.trace { "LibSSH2Native::Channel::close()" }
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
        buf.size
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
    ): Int {
      buffer.usePinned { pinned ->

        // Get the raw pointer address of the specified offset in our byte array
        val targetAddress: CPointer<ByteVar> = pinned.addressOf(0)

        while (true) {
          // Call the actual C function directly like a regular Kotlin function!
          // libssh2_channel_read_ex returns a sign-extended long (ssize_t)
          val bytesRead = libssh2_channel_read_ex(
            channel = channelPtr.toCPointer(),
            stream_id = streamId,
            buf = targetAddress,
            buflen = buffer.size.convert()
          )


          when {
            bytesRead > 0 -> {
              return bytesRead.toInt() // Return read amount
            }

            bytesRead == 0L -> {
              return 0 // End of file (EOF)
            }

            bytesRead == LIBSSH2_ERROR_EAGAIN.toLong() -> {
              // Non-blocking catch: Yield control back to the coroutine dispatcher
              // instead of freezing the OS thread.
              log.trace { "channelRead() libssh2_channel_read_ex() returned LIBSSH2_ERROR_EAGAIN" }
              waitSocket(session,socketHandle)
              //waitSocket(session, channelPtr)
              //sleep(10.convert())
            }

            else -> {
              throw IllegalStateException("Libssh2 read failed with native error code: $bytesRead")
            }
          }
        }
        @Suppress("KotlinUnreachableCode")
        throw IllegalStateException("Unreachable code")
      }
    }
  }
}

