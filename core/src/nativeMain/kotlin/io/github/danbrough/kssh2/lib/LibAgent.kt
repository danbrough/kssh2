package io.github.danbrough.kssh2.lib

import io.github.danbrough.libssh2.cinterop.libssh2_agent_disconnect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_free
import kotlinx.cinterop.toCPointer

actual object LibAgent {
  actual fun close(agent: AgentPtr) {
    if (agent != 0L) {
      println("calling libssh2_agent_disconnect ...")
      libssh2_agent_disconnect(agent.toCPointer())
      println("calling libssh2_agent_free ...")
      libssh2_agent_free(agent.toCPointer())
    }
  }
}
