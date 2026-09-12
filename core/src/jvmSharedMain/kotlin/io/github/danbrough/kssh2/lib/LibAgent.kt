package io.github.danbrough.kssh2.lib

actual object LibAgent {
  @JvmStatic
  actual external fun close(agent: AgentPtr)
}