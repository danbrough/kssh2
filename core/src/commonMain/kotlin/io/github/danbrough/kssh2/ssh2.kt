package io.github.danbrough.kssh2

import org.danbrough.klog.logger


interface Scope : AutoCloseable{

}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class SSH2DSL


internal val sshLog = logger("SSH")

