package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.lib.LibSSH2.Agent
import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.LIBSSH2_SESSION
import io.github.danbrough.libssh2.cinterop.SSH_DISCONNECT_BY_APPLICATION
import io.github.danbrough.libssh2.cinterop.libssh2_agent_connect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_get_identity
import io.github.danbrough.libssh2.cinterop.libssh2_agent_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_list_identities
import io.github.danbrough.libssh2.cinterop.libssh2_agent_publickey
import io.github.danbrough.libssh2.cinterop.libssh2_agent_userauth
import io.github.danbrough.libssh2.cinterop.libssh2_session_disconnect_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_free
import io.github.danbrough.libssh2.cinterop.libssh2_session_handshake
import io.github.danbrough.libssh2.cinterop.libssh2_session_init_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_error
import io.github.danbrough.libssh2.cinterop.libssh2_session_set_blocking
import io.github.danbrough.libssh2.cinterop.libssh2_userauth_password_ex
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


actual object LibSession {
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