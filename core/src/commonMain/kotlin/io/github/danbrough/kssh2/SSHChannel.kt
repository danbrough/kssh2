package io.github.danbrough.kssh2

import io.github.danbrough.kssh2.lib.ChannelPtr
import io.github.danbrough.kssh2.lib.LibSSH2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.danbrough.klog.logger


private val channelLog = logger("SSH2")

class Channel(val session: Session, channelType: String = "session") : Scope {

  var channelPtr: ChannelPtr =
    LibSSH2.Channel.channelOpen(session.session, session.socket, channelType)


  suspend fun requestPty(terminal: String = "vanilla") {
    LibSSH2.Channel.requestPty(channelPtr, terminal).also {
      if (it != 0L)
        error("libssh2_channel_request_pty() failed. returned $it")
    }
  }

  suspend fun processStartup(request: String, message: String) {
    LibSSH2.Channel.processStartup(session.session, session.socket, channelPtr, request, message)
      .also {
        if (it != 0L) error("LibSSH2.Channel.processStartup() returned $it")
      }
  }

  suspend fun exec(cmdLine: String) =
    withContext(Dispatchers.IO) {
      processStartup("exec", cmdLine)
    }


  fun readChannel(bufSize: Int = 0x4000): Flow<ByteArray> = flow {
    val buf = ByteArray(bufSize)
    while (true) {
      val ret = LibSSH2.Channel.read(session.session, session.socket, channelPtr, 0, buf)
      channelLog.trace { "readChannel():${SshUtils.threadName()} ret: $ret " }
      if (ret <= 0) break
      emit(buf.take(ret).toByteArray())
    }

    channelLog.trace { "readChannel() done ${SshUtils.threadName()}" }
  }.flowOn(Dispatchers.IO).buffer(0)

  override fun close() {
    LibSSH2.Channel.close(channelPtr)
  }
}

suspend fun <R> Session.channel(
  channelType: String = "session",
  block: suspend Channel.() -> R
): R = Channel(this, channelType).let { channel ->
  try {
    return channel.block()
  } finally {
    channel.close()
  }
}