package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.lib.AgentPtr
import io.github.danbrough.kssh2.lib.LibSSH2
import io.github.danbrough.kssh2.lib.LibSession
import io.github.danbrough.kssh2.lib.LibSocket
import io.github.danbrough.kssh2.lib.SSH2Result
import io.github.danbrough.kssh2.lib.SessionPtr
import io.github.danbrough.kssh2.lib.SocketHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

private val log = sshLog

@SSH2DSL
class Session(override val key: CoroutineContext.Key<*>, val scope: SSHScope) : Scope,
  CoroutineContext.Element {

  data class SessionKey(val name: String = "") : CoroutineContext.Key<Session> {
    companion object {
      val DEFAULT = SessionKey()
    }
  }

  /**
   * Key for the current session
   */
  companion object : CoroutineContext.Key<Session>

  constructor(name: String, scope: SSHScope) : this(SessionKey(name), scope)


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
    if (agent != 0L) {
      log.trace { "Session::close() $this .. closing agent.." }
      LibSSH2.Agent.close(agent)
      agent = 0L
    }
    log.trace { "Session::close() $this.. closing socket.." }
    LibSocket.close(socket)
    log.trace { "Session::close() $this.. closing session.." }
    LibSession.close(session)
    log.trace { "Session::close() $this finished" }
  }
}


internal fun resultOf(sessionPtr: SessionPtr, success: Boolean) =
  if (success) SSH2Result.SUCCESS else SSH2Result(-1, LibSession.getError(sessionPtr))


suspend fun <R> session(
  name: String,
  block: suspend Session.() -> R
): R = session(Session.SessionKey(name), block)

suspend fun <R> session(
  key: CoroutineContext.Key<Session> = Session,
  block: suspend Session.() -> R
): R =

  currentCoroutineContext()[key]?.block() ?: ssh {
    Session(key, this).let { session ->
      log.warn { "created session: $session" }
      withContext(session) {
        sshScope(block, session)
      }
    }


  }





