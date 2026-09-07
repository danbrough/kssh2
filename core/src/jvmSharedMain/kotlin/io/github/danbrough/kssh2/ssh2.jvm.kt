package io.github.danbrough.kssh2

import org.danbrough.klog.logger


internal val log = logger("SSH2_JVM")
actual class SSHSessionOld() : Scope {
  actual override fun close() {
  }
}