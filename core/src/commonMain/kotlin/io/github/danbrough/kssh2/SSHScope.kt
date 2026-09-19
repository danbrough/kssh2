package io.github.danbrough.kssh2


import io.github.danbrough.kssh2.lib.LibSSH2
import kotlin.coroutines.CoroutineContext


@SSH2DSL
class SSHScope : CoroutineContext.Element {

  companion object : CoroutineContext.Key<SSHScope>{
    init {
      println("SSHScope::initLib()")
      LibSSH2.initLib()
      println("REGISTERING SSHSCOPE SHUTDOWN HOOK")
      SshUtils.atExit{
        globalSSH.close()
      }
    }
  }

  override val key: CoroutineContext.Key<*> = SSHScope


  fun close() = LibSSH2.closeLib()

  @Deprecated(
    message = "Nested 'ssh' blocks are not allowed.",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("error(\"Forbidden at compile-time.\")")
  )

  fun ssh(block: SSHScope.() -> Unit): Nothing =
    error("Forbidden at compile-time.")
}

private val globalSSH = SSHScope()


suspend fun <R> ssh(block: suspend SSHScope.() -> R): R =
  globalSSH.block()


suspend fun <P, C : Scope, R> P.sshScope(block: suspend C.() -> R, childScope: C): R =
  runCatching {
    childScope.block()
  }.also { childScope.close() }.getOrThrow()

