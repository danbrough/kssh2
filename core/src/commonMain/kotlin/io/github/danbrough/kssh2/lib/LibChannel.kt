package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.flow.Flow

expect object LibChannel {
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