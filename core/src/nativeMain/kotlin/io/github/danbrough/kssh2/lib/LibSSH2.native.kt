package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.kssh2_exit
import io.github.danbrough.libssh2.cinterop.kssh2_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_disconnect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_free
import kotlinx.cinterop.toCPointer


actual object LibSSH2 {

  actual fun initJNI() {}
  actual val Session = LibSession
  actual val Socket = LibSocket
  actual val Channel = LibChannel

  actual fun initLib() {
    kssh2_init(0).also {
      logNative.debug { "LibSSH2Native::initLib() kssh2_init() returned: $it" }
    }

  }

  actual fun closeLib() {
    logNative.trace { "LibSSH2Native::closeLib() calling kssh2_exit()" }
    kssh2_exit()
  }


  actual object Agent {
    actual fun close(agent: AgentPtr) {
      if (agent != 0L) {
        libssh2_agent_disconnect(agent.toCPointer());
        libssh2_agent_free(agent.toCPointer());
      }
    }
  }


}

