package io.github.danbrough.kssh2


import io.github.danbrough.kssh2.lib.LibSSH2
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


@SSH2DSL
class SSHScope : Scope, CoroutineContext.Element {

  companion object : CoroutineContext.Key<SSHScope>

  override val key: CoroutineContext.Key<*> = SSHScope

  init {
    LibSSH2.initLib()
  }

  override fun close() = LibSSH2.closeLib()

  @Deprecated(
    message = "Nested 'ssh' blocks are not allowed.",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("error(\"Forbidden at compile-time.\")")
  )

  fun ssh(block: SSHScope.() -> Unit): Nothing =
    error("Forbidden at compile-time.")
}

private val globalSsh: Lazy<SSHScope> = lazy { SSHScope() }

suspend fun <R> ssh(block: suspend SSHScope.() -> R): R =
  currentCoroutineContext()[SSHScope]?.block() ?: run {
    if (!globalSsh.isInitialized())
      SshUtils.atExit {
        globalSsh.value.close()
      }

    globalSsh.value.let {
      withContext(it) {
        it.block()
      }
    }
  }


suspend fun <P : Scope, C : Scope, R> P.sshScope(block: suspend C.() -> R, childScope: C): R =
  runCatching {
    childScope.block()
  }.also { childScope.close() }.getOrThrow()

