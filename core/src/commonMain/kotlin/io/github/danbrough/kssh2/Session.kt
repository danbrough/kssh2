package io.github.danbrough.kssh2

import kotlinx.coroutines.withContext

suspend fun <R> SSHScope.session(block: suspend Session.() -> R): R =
  withContext(this) {
    sshScope(block, Session())
  }

class Session() : Scope {

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

  override fun close() {
    LibSSH2.Agent.close(agent)
    LibSSH2.Socket.close(socket)
    LibSSH2.Session.close(session)
  }
}