package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.SshUtils.getEnv
import io.github.danbrough.kssh2.lib.LibSSH2
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_AUTHENTICATION_FAILED
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_FILE
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_KEYFILE_AUTH_FAILED
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_PUBLICKEY_UNVERIFIED
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_SOCKET_SEND
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_SOCKET_TIMEOUT
import io.github.danbrough.libssh2.cinterop.LIBSSH2_INVALID_SOCKET
import io.github.danbrough.libssh2.cinterop.LIBSSH2_SESSION
import io.github.danbrough.libssh2.cinterop.SSH_DISCONNECT_BY_APPLICATION
import io.github.danbrough.libssh2.cinterop.libssh2_agent_connect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_disconnect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_free
import io.github.danbrough.libssh2.cinterop.libssh2_agent_get_identity
import io.github.danbrough.libssh2.cinterop.libssh2_agent_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_list_identities
import io.github.danbrough.libssh2.cinterop.libssh2_agent_publickey
import io.github.danbrough.libssh2.cinterop.libssh2_agent_userauth
import io.github.danbrough.libssh2.cinterop.libssh2_session_disconnect_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_free
import io.github.danbrough.libssh2.cinterop.libssh2_session_handshake
import io.github.danbrough.libssh2.cinterop.libssh2_session_init_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_set_blocking
import io.github.danbrough.libssh2.cinterop.libssh2_socket_t
import io.github.danbrough.libssh2.cinterop.libssh2_userauth_publickey_fromfile_ex
import io.github.danbrough.libssh2.cinterop.ssh2_socket_close
import io.github.danbrough.libssh2.cinterop.waitsocket
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value

private val log = logNative

actual class SSHSessionOld(val useAgent: Boolean, val ssh: SSHScope) : Scope {
  var sock: libssh2_socket_t = LIBSSH2_INVALID_SOCKET
  var session: CPointer<LIBSSH2_SESSION>? = null

  var agent: CPointer<cnames.structs._LIBSSH2_AGENT>? = null
  fun connect(hostName: String, port: Int = 22) {
    log.debug { "connect(): ${hostName}:$port" }
    LibSSH2.Socket.connect(hostName, port).also {
      if (it == LIBSSH2_INVALID_SOCKET.toLong()) error("ssh2_socket_connect ${hostName}:$port failed")
      sock = it.convert()
    }


    session =
      libssh2_session_init_ex(null, null, null, null)
        ?: error("Failed to created ssh session")

    libssh2_session_set_blocking(session, 0)

    //libssh2_trace(session, 0)

    var rc = 0
    do {
      rc = libssh2_session_handshake(session, sock)
    } while (rc == LIBSSH2_ERROR_EAGAIN)
    if (rc != 0) error("libssh2_session_handshake(session, sock) failed. returned: $rc")
  }

  suspend fun authenticateWithAgent(remoteUser: String) {
    log.trace { "authenticateWithAgent(): remoteUser: $remoteUser" }
    agent?.also {
      log.trace { "agent already initialized" }
      return
    }
    session ?: error("session is null. Call connect first")

    log.trace { "session: $session" }

    agent =
      libssh2_agent_init(session) ?: error("libssh2_agent_init() failed")
    log.trace { "got agent: $agent" }
    if (libssh2_agent_connect(agent) != 0)
      error("libssh2_agent_connect() failed")
    log.trace { "connected to agent" }

    libssh2_agent_list_identities(agent).also {
      if (it != 0) error("libssh2_agent_list_identities failed err: $it")
    }

    memScoped {
      val identityVar = alloc<CPointerVar<libssh2_agent_publickey>>()
      identityVar.value = null


      var prev: CPointer<libssh2_agent_publickey>? = null
      var success = false
      while (libssh2_agent_get_identity(agent, identityVar.ptr, prev) == 0) {
        log.trace { "trying to authenticate with identity: ${identityVar.pointed?.comment?.toKString()}" }
        // 4. Try authenticating with the current identity
        libssh2_agent_userauth(agent, remoteUser, identityVar.value).also {
          if (it == 0) {
            println("Authentication successful!")
            success = true
            break
          } else {
            println("Failed err: $it  LIBSSH2_ERROR_EAGAIN = $LIBSSH2_ERROR_EAGAIN")
            if (it == LIBSSH2_ERROR_EAGAIN) {
              waitSocket()
              continue
            }
          }
        }

        // 5. Update prev for the next iteration step
        prev = identityVar.value
      }

      if (!success) error("Failed to authenticate with any identity")
    }

  }

  fun authenticatePubKey(
    user: String = getEnv("USER") ?: error("user not specified"),
    publicKeyPath: String? = null,
    privateKeyPath: String? = null,
    password: String? = null
  ) {
    log.debug { "authenticatePublicKey()" }

    if (session == null) error("session is null")

    /*
    config.user, config.user.length.convert(),
     */
    var rc = 0
    do {
      rc = libssh2_userauth_publickey_fromfile_ex(
        session,
        user,
        user.length.convert(),
        publicKeyPath,
        privateKeyPath,
        password
      )
    } while (rc == LIBSSH2_ERROR_EAGAIN)

    log.debug { "libssh2_userauth_publickey_fromfile_ex returned $rc" }
    /*
    LIBSSH2_ERROR_ALLOC - An internal memory allocation call failed.

LIBSSH2_ERROR_SOCKET_SEND - Unable to send data on socket.

LIBSSH2_ERROR_SOCKET_TIMEOUT -

LIBSSH2_ERROR_PUBLICKEY_UNVERIFIED - The username/public key combination was invalid.

LIBSSH2_ERROR_AUTHENTICATION_FAILED -
     */
    val message = when (rc) {
      0 -> "SUCCESS"
      LIBSSH2_ERROR_AUTHENTICATION_FAILED -> "LIBSSH2_ERROR_AUTHENTICATION_FAILED"
      LIBSSH2_ERROR_SOCKET_TIMEOUT -> "LIBSSH2_ERROR_SOCKET_TIMEOUT"
      LIBSSH2_ERROR_PUBLICKEY_UNVERIFIED -> "LIBSSH2_ERROR_PUBLICKEY_UNVERIFIED"
      LIBSSH2_ERROR_SOCKET_SEND -> "LIBSSH2_ERROR_SOCKET_SEND"
      LIBSSH2_ERROR_KEYFILE_AUTH_FAILED -> "LIBSSH2_ERROR_KEYFILE_AUTH_FAILED"
      LIBSSH2_ERROR_FILE -> "LIBSSH2_ERROR_FILE publicKey:$publicKeyPath privateKey:$privateKeyPath"
      else -> "UNKNOWN ERROR"
    }

    if (rc != 0) error("authenticate failed: $message")

    /*
int libssh2_userauth_publickey_fromfile_ex(LIBSSH2_SESSION *session,
                                           const char *username,
                                           unsigned int ousername_len,
                                           const char *publickey,
                                           const char *privatekey,
                                           const char *passphrase);
DESCRIPTION
session - Session instance as returned by libssh2_session_init_ex
username - Pointer to user name to authenticate as.
username_len - Length of username.
publickey - Path name of the public key file. (e.g. /etc/ssh/hostkey.pub). If libssh2 is built against OpenSSL, this option can be set to NULL.
privatekey - Path name of the private key file. (e.g. /etc/ssh/hostkey)
passphrase - Passphrase to use when decoding privatekey.
Attempt public key authentication using a PEM encoded private key file stored on disk

      var rc = 0
      do {
        rc = libssh2_userauth_publickey_fromfile_ex(
          session, user, user.length.convert(),
          null, confiprivateKeyFile!!, config.password!!
        )
      } while (rc == LIBSSH2_ERROR_EAGAIN)

      log.debug { "libssh2_userauth_publickey_fromfile_ex returned $rc" }
      return rc == 0
*/
  }


  /*
  fun openChannel(): Channel {
  var rc: Int
  var channel: CPointer<LIBSSH2_CHANNEL>?
  val channelType =
    "session" // Channel type to open. Typically one of session, direct-tcpip, or tcpip-forward.
  while (true) {
    /*
session - Session instance as returned by libssh2_session_init_ex
channel_type - Channel type to open. Typically one of session, direct-tcpip, or tcpip-forward. The SSH2 protocol allowed for additional types including local, custom channel types.
channel_type_len - Length of channel_type
window_size - Maximum amount of unacknowledged data remote host is allowed to send before receiving an SSH_MSG_CHANNEL_WINDOW_ADJUST packet.
packet_size - Maximum number of bytes remote host is allowed to send in a single SSH_MSG_CHANNEL_DATA or SSG_MSG_CHANNEL_EXTENDED_DATA packet.
message - Additional data as required by the selected channel_type.
message_len - Length of message parameter.
Allocate a new channel for exchanging data with the server.
This method is typically called through its macroized form: libssh2_channel_open_session
or via libssh2_channel_direct_tcpip or libssh2_channel_forward_listen
     */
    channel = libssh2_channel_open_ex(
      session,
      channelType,
      channelType.length.convert(),
      LIBSSH2_CHANNEL_WINDOW_DEFAULT.convert(),
      LIBSSH2_CHANNEL_PACKET_DEFAULT.convert(),
      null,
      0.convert()
    )

    if (channel != null) return Channel(this, channel)
    rc = libssh2_session_last_errno(session)
    if (rc != LIBSSH2_ERROR_EAGAIN) break
    waitSocket()
  }
  error("Failed to open channel: rc:$rc")
}
 */





  actual override fun close() {
    log.trace { "SSHSession::close()" }
    if (agent != null) {
      libssh2_agent_disconnect(agent)
      libssh2_agent_free(agent)
      log.trace { "closed agent" }
    }
    if (session != null) {
      libssh2_session_disconnect_ex(session, SSH_DISCONNECT_BY_APPLICATION, "Normal Shutdown", "")
      libssh2_session_free(session)
      log.trace { "closed session" }
    }

    if (sock != LIBSSH2_INVALID_SOCKET) {
      ssh2_socket_close(sock)
      log.trace { "closed socket" }
    }
  }

  fun waitSocket() = waitsocket(sock, session)


}