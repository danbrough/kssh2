package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.danbrough.klog.logger
import java.nio.ByteBuffer
import java.util.Locale
import java.util.Locale.getDefault

@Suppress("UnsafeDynamicallyLoadedCode")
actual object LibSSH2 {

  val log = logger("SSH2")

  init {
    try {
      /*
            listOf(
              "/usr/local/opt/openssl@3/lib/libssl.3.dylib",
              "/usr/local/opt/openssl@3/lib/libcrypto.3.dylib",
              "/usr/local/opt/libssh2/lib/libssh2.1.dylib",
              "/Users/dan/workspace/kssh2/core/build/bin/macosX64/kssh2DebugShared/libkssh2.dylib",

            ).forEach {
              log.debug { "loading $it" }
              System.load(it)
            }*/

      if (System.getProperty("os.name").lowercase(getDefault()).contains("mac")){
        log.info { "LibSSH2::running on mac" }


      }

      val libs = System.getenv("SSH2_LIBS")?.split(":") ?: emptyList()
      libs.forEach { lib->
        log.warn { "loading directly lib: $lib.." }
        System.load(lib)
      }



/*      log.debug { "JNISupport: loading ssh2 library.." }
      System.loadLibrary("ssh2")
      log.debug { "JNISupport: loading kssh2 library.." }
      System.loadLibrary("kssh2")
      log.debug { "JNISupport: kssh2 library loaded" }*/

    } catch (e: UnsatisfiedLinkError) {
      log.error(e) { "Failed to load library" }
      throw e
    }
  }

  @JvmStatic
  actual external fun initJNI()

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
    actual fun getError(session: SessionPtr): String = getErrorJNI(session) ?: "Unknown Error"

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