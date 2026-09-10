package demo.test.lib

actual object Session {
  @JvmStatic
  actual external fun createSession(blocking: Boolean): SessionPtr

  @JvmStatic
  actual external fun close(session: SessionPtr)
}