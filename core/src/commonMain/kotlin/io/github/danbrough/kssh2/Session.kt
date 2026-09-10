package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.lib.AgentPtr
import io.github.danbrough.kssh2.lib.LibSSH2
import io.github.danbrough.kssh2.lib.SSH2Result
import io.github.danbrough.kssh2.lib.SessionPtr
import io.github.danbrough.kssh2.lib.SocketHandle
import kotlinx.coroutines.withContext

suspend fun <R> SSHScope.session(block: suspend Session.() -> R): R =
  withContext(this) {
    sshScope(block, Session())
  }

class Session() : Scope {


  val session: SessionPtr = LibSSH2.Session.createSession(false)
  var socket: SocketHandle = 0L
  var agent: AgentPtr = 0L


  suspend fun connect(host: String, port: Int = 22): SSH2Result {
    LibSSH2.Socket.close(socket)
    socket = LibSSH2.Socket.connect(host, port)
    return if (socket == 0L) resultOf(session,false) else
      LibSSH2.Session.sessionHandshake(session, socket).asResult()
  }

  suspend fun authenticateWithAgent(remoteUser: String): SSH2Result {
    agent = LibSSH2.Session.authenticateWithAgent(session, socket, remoteUser)
    return resultOf(session,agent != 0L)
  }

  suspend fun authenticatePassword(userName: String, password: String): SSH2Result =
    LibSSH2.Session.authenticatePassword(session, socket, userName, password).asResult()


  private fun Int.asResult(): SSH2Result =
    if (this == 0) SSH2Result.SUCCESS else SSH2Result(this, LibSSH2.Session.getError(session))

  private fun Long.asResult(): SSH2Result = toInt().asResult()

  override fun close() {
    LibSSH2.Agent.close(agent)
    LibSSH2.Socket.close(socket)
    LibSSH2.Session.close(session)
  }
}


internal fun resultOf(sessionPtr: SessionPtr,success: Boolean) = if (success) SSH2Result.SUCCESS else SSH2Result(-1, LibSSH2.Session.getError(sessionPtr))
