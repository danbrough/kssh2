package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.danbrough.klog.logger
import java.nio.ByteBuffer

actual object LibSSH2 {

  val log = logger("SSH2")

  init {
    try {
      log.debug { "JNISupport: loading ssh2 library.." }
      System.loadLibrary("ssh2")
      log.debug { "JNISupport: loading kssh2 library.." }
      System.loadLibrary("kssh2")
      log.debug { "JNISupport: kssh2 library loaded" }
    } catch (e: UnsatisfiedLinkError) {
      log.error(e) { "Failed to load library" }
      throw e
    }
  }

  @JvmStatic
  actual external fun initLib()

  @JvmStatic
  actual external fun closeLib()

  @JvmStatic
  external fun test1()

  actual object Socket {
    @JvmStatic
    actual external fun connect(hostName: String, port: Int): SocketHandle

    @JvmStatic
    actual external fun close(socket: SocketHandle)
  }

  actual object Session {
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
    actual  fun getError(session: SessionPtr): String = getErrorJNI(session) ?: "Unknown Error"

  }

  actual object Agent {
    @JvmStatic
    actual external fun close(agent: AgentPtr)
  }

  actual object Channel {
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
}