package io.github.danbrough.kssh2

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext


interface Scope : AutoCloseable

private var globalScope: SSHScope? = null


suspend fun <R> ssh(block: suspend SSHScope.() -> R): R =
  currentCoroutineContext()[SSHScope.ContextKey]?.block() ?: globalScope?.block()
  ?: SSHScope().let { scope ->
    globalScope = scope
    currentCoroutineContext().job.invokeOnCompletion {
      scope.close()
    }

    withContext(scope) {
      scope.block()
    }
  }


suspend fun <P : Scope, C : Scope, R> P.sshScope(block: suspend C.() -> R, childScope: C): R =
  runCatching {
    childScope.block()
  }.also { childScope.close() }.getOrThrow()


expect class SSHSessionOld : Scope {
  override fun close()
}

