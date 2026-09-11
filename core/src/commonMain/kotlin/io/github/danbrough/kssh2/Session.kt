package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.lib.AgentPtr
import io.github.danbrough.kssh2.lib.LibSSH2
import io.github.danbrough.kssh2.lib.LibSession
import io.github.danbrough.kssh2.lib.LibSocket
import io.github.danbrough.kssh2.lib.SSH2Result
import io.github.danbrough.kssh2.lib.SessionPtr
import io.github.danbrough.kssh2.lib.SocketHandle
import kotlinx.coroutines.withContext

suspend fun <R> SSHScope.session(block: suspend Session.() -> R): R =
  withContext(this) {
    sshScope(block, Session())
  }

class Session() : Scope {


  val session: SessionPtr = LibSession.createSession(false)
  var socket: SocketHandle = 0L
  var agent: AgentPtr = 0L


  fun connect(host: String, port: Int = 22): SSH2Result {
    LibSocket.close(socket)
    socket = LibSocket.connect(host, port)
    return if (socket == 0L) resultOf(session, false) else
      LibSession.sessionHandshake(session, socket).asResult()
  }

  fun authenticateWithAgent(remoteUser: String): SSH2Result {
    agent = LibSession.authenticateWithAgent(session, socket, remoteUser)
    return resultOf(session, agent != 0L)
  }

  fun authenticatePassword(userName: String, password: String): SSH2Result =
    LibSession.authenticatePassword(session, socket, userName, password).asResult()

  fun authenticatePublicKey(
    userName: String,
    publicKeyData: String?,
    privateKeyData: String?,
    password: String? = null
  ): SSH2Result =
    LibSession.authenticatePublicKey(
      session,
      socket,
      userName,
      publicKeyData,
      privateKeyData,
      password
    ).asResult()


  private fun Int.asResult(): SSH2Result =
    if (this == 0) SSH2Result.SUCCESS else SSH2Result(this, LibSession.getError(session))

  private fun Long.asResult(): SSH2Result = toInt().asResult()

  override fun close() {
    LibSSH2.Agent.close(agent)
    LibSocket.close(socket)
    LibSession.close(session)
  }
}


internal fun resultOf(sessionPtr: SessionPtr, success: Boolean) =
  if (success) SSH2Result.SUCCESS else SSH2Result(-1, LibSession.getError(sessionPtr))
