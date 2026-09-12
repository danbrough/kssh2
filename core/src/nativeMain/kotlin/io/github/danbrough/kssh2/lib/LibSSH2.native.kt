package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.kssh2_exit
import io.github.danbrough.libssh2.cinterop.kssh2_init


actual object LibSSH2 {

  actual fun initJNI() {}
  actual val Session = LibSession
  actual val Socket = LibSocket
  actual val Channel = LibChannel
  actual val Agent = LibAgent

  actual fun initLib() {
    kssh2_init(0).also {
      logNative.debug { "LibSSH2Native::initLib() kssh2_init() returned: $it" }
    }
  }

  actual fun closeLib() {
    logNative.trace { "LibSSH2Native::closeLib() calling kssh2_exit()" }
    kssh2_exit()
  }
}

