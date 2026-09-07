package io.github.danbrough.kssh2

import kotlin.coroutines.CoroutineContext


class SSHScope : Scope, CoroutineContext.Element {
  companion object ContextKey : CoroutineContext.Key<SSHScope>

  init {
    LibSSH2.initLib()
  }

  override fun close() = LibSSH2.closeLib()

  override val key: CoroutineContext.Key<*> = ContextKey
}

