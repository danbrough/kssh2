package io.github.danbrough.kssh2.lib

expect object LibSession {

  fun createSession(blocking: Boolean): SessionPtr

  fun close(session: SessionPtr)

  fun sessionHandshake(session: SessionPtr, socket: SocketHandle): Int

  fun waitSocket(session: SessionPtr, socket: SocketHandle): Long

  fun getError(session: SessionPtr): String

  fun authenticateWithAgent(
    session: SessionPtr,
    socket: SocketHandle,
    remoteUser: String
  ): AgentPtr

  fun authenticatePassword(
    sessionPtr: SessionPtr,
    socket: SocketHandle, userName: String, password: String
  ): Int

  fun authenticatePublicKey(
    sessionPtr: SessionPtr,
    socket: SocketHandle,
    user: String?,
    publicKeyData: String?,
    privateKeyData: String?,
    passphrase: String?
  ): Int


}