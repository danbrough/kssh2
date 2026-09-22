package io.github.danbrough.kssh2


import io.github.danbrough.katty.KattyUtils
import io.github.danbrough.kssh2.lib.LibSSH2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


@SSH2DSL
class SSHScope : CoroutineContext.Element {

  companion object : CoroutineContext.Key<SSHScope> {
    init {
      sshLog.info { "SSHScope::initLib()" }
      LibSSH2.initLib()
      KattyUtils.atExit {
        globalSSH.close()
      }
    }
  }

  override val key: CoroutineContext.Key<*> = SSHScope

  var message: String = "Hello World"

  fun close() = LibSSH2.closeLib()

  private val sessionJob = SupervisorJob()
  private val sessionScope = CoroutineScope(sessionJob + this)

  fun <R> withSession(key: Session.SessionKey, block: suspend Session.() -> R): Deferred<R> =
    Session(key, this).run {
      this@SSHScope.sessionScope.async(this) {
        block()
      }
    }


  @Deprecated(
    message = "Nested 'ssh' blocks are not allowed.",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("error(\"Forbidden at compile-time.\")")
  )

  fun ssh(block: SSHScope.() -> Unit): Nothing =
    error("Forbidden at compile-time.")


}

private val globalSSH = SSHScope()


suspend fun <R> ssh(
  block: suspend SSHScope.() -> R
): R =
  currentCoroutineContext()[SSHScope]?.block() ?: withContext(globalSSH) {
    globalSSH.block()
  }


suspend fun <P, C : Scope, R> P.sshScope(block: suspend C.() -> R, childScope: C): R =
  runCatching {
    childScope.block()
  }.also { childScope.close() }.getOrThrow()

