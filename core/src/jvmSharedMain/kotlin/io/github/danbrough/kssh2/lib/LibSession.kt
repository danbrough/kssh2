package io.github.danbrough.kssh2.lib

actual object LibSession {
  @JvmStatic
  actual external fun createSession(blocking: Boolean): SessionPtr

  @JvmStatic
  actual external fun close(session: SessionPtr)


  @JvmStatic
  actual external fun sessionHandshake(session: SessionPtr, socket: SocketHandle): Int

  @JvmStatic
  actual external fun waitSocket(session: SessionPtr, socket: SocketHandle): Long


  @JvmStatic
  actual external fun authenticateWithAgent(
    session: SessionPtr,
    socket: SocketHandle,
    remoteUser: String
  ): AgentPtr

  @JvmStatic
  actual external fun authenticatePassword(
    sessionPtr: Long,
    socket: Long,
    userName: String,
    password: String
  ): Int

  @JvmStatic
  actual external fun authenticatePublicKey(
    sessionPtr: Long,
    socket: Long,
    user: String?,
    publicKeyData: String?,
    privateKeyData: String?,
    passphrase: String?
  ): Int

  private external fun getErrorJNI(session: SessionPtr): String?
  actual fun getError(session: SessionPtr): String = getErrorJNI(session) ?: "Unknown Error"

}
