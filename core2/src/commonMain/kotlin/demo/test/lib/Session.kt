package demo.test.lib

expect object Session {
  fun createSession(blocking: Boolean): SessionPtr

  fun close(session: SessionPtr)
}