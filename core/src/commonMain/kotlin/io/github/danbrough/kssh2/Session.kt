package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.lib.AgentPtr
import io.github.danbrough.kssh2.lib.LibSSH2
import io.github.danbrough.kssh2.lib.SessionPtr
import io.github.danbrough.kssh2.lib.SocketHandle
import kotlinx.coroutines.withContext

suspend fun <R> SSHScope.session(block: suspend Session.() -> R): R =
  withContext(this) {
    sshScope(block, Session())
  }

class Session() : Scope {

  class SessionException(val code: Int, override val message: String) : Exception(message)

  val session: SessionPtr = LibSSH2.Session.createSession(false)
  var socket: SocketHandle = 0L
  var agent: AgentPtr = 0L


  suspend fun connect(host: String, port: Int = 22): Long {
    LibSSH2.Socket.close(socket)
    socket = LibSSH2.Socket.connect(host, port)
    return LibSSH2.Session.sessionHandshake(session, socket)
  }

  suspend fun authenticateWithAgent(remoteUser: String) {
    agent = LibSSH2.Session.authenticateWithAgent(session, socket, remoteUser)
  }

  suspend fun authenticatePassword(userName: String, password: String) =
    LibSSH2.Session.authenticatePassword(session, socket, userName, password).throwErrorIfNeeded()


  private fun Int.throwErrorIfNeeded() {
    if (this != 0) throw SessionException(
      this,
      LibSSH2.Session.getError(session) ?: "Unknown error"
    )
  }


  override fun close() {
    LibSSH2.Agent.close(agent)
    LibSSH2.Socket.close(socket)
    LibSSH2.Session.close(session)
  }
}