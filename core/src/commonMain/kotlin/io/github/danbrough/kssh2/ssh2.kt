package io.github.danbrough.kssh2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext

import kotlinx.coroutines.SupervisorJob



interface Scope : AutoCloseable

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class SSH2DSL




