package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer

actual object LibChannel {
  @JvmStatic
  actual external fun channelOpen(
    session: SessionPtr,
    socket: SocketHandle,
    channelType: String,
    windowSize: Int,
    packetSize: Int,
    message: String?
  ): ChannelPtr

  @JvmStatic
  actual external fun close(channel: ChannelPtr)

  @JvmStatic
  actual external fun requestPty(channelPtr: ChannelPtr, terminal: String): Long

  @JvmStatic
  actual external fun write(channel: ChannelPtr, data: String): Long

  actual external fun processStartup(
    sessionPtr: SessionPtr,
    socketHandle: SocketHandle,
    channel: ChannelPtr,
    request: String,
    message: String
  ): Long

  @Suppress("NewApi")
  actual suspend fun readAll(
    channelPtr: ChannelPtr
  ): Flow<ByteArray> = flow {

    val buf = ByteBuffer.allocateDirect(32)
    val sshChannel = SshChannelJVM(channelPtr)

    while (true) {
      val read = sshChannel.read(0, buf)
      if (read <= 0) break
      buf.flip()
      val bytes = ByteArray(read)
      buf.get(bytes)
      emit(bytes)
      buf.clear()
    }
  }

  actual external fun read(
    session: SessionPtr,
    socketHandle: SocketHandle,
    channelPtr: ChannelPtr,
    streamId: Int,
    buffer: ByteArray,
  ): Long
}