package io.github.danbrough.kssh2


import io.github.danbrough.kssh2.lib.LibSSH2
import kotlin.coroutines.CoroutineContext


@SSH2DSL
class SSHScope : Scope, CoroutineContext.Element {
  companion object ContextKey : CoroutineContext.Key<SSHScope>

  init {
    LibSSH2.initLib()
  }

  override fun close() = LibSSH2.closeLib()

  override val key: CoroutineContext.Key<*> = ContextKey

  @Deprecated(
    message = "Nested 'ssh' blocks are not allowed.",
    level = DeprecationLevel.ERROR
  )
  fun ssh(block: SSHScope.() -> Unit): Nothing =
    error("Forbidden at compile-time.")
}

