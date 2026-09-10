package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.flow.Flow


typealias SocketHandle = Long
typealias SessionPtr = Long
typealias AgentPtr = Long
typealias ChannelPtr = Long


private const val LIBSSH2_CHANNEL_WINDOW_DEFAULT = 2 * 1024 * 1024
private const val LIBSSH2_CHANNEL_PACKET_DEFAULT = 32768



expect object LibSSH2 {

  fun initJNI()

  fun initLib()
  fun closeLib()


  object Socket {
    fun connect(hostName: String, port: Int = 22): SocketHandle

    fun close(socket: SocketHandle)
  }

  object Session {

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

  object Channel {
    fun channelOpen(
      session: SessionPtr,
      socket: SocketHandle,
      //Channel type to open. Typically one of session, direct-tcpip, or tcpip-forward. The SSH2 protocol allowed for additional types including local, custom channel types.
      channelType: String = "session",
      windowSize: Int = LIBSSH2_CHANNEL_WINDOW_DEFAULT,
      packetSize: Int = LIBSSH2_CHANNEL_PACKET_DEFAULT,
      message: String? = null
    ): ChannelPtr

    fun requestPty(channelPtr: ChannelPtr, terminal: String): Long

    fun processStartup(
      sessionPtr: SessionPtr,
      socketHandle: SocketHandle,
      channel: ChannelPtr,
      request: String,
      message: String
    ): Long

    fun read(
      session: SessionPtr,
      socketHandle: SocketHandle,
      channelPtr: ChannelPtr,
      streamId: Int,
      buffer: ByteArray,
    ): Long

    suspend fun readAll(channelPtr: ChannelPtr): Flow<ByteArray>
    fun write(channel: ChannelPtr, data: String): Long

    fun close(channel: ChannelPtr)
  }

  object Agent {
    fun close(agent: AgentPtr)
  }
}