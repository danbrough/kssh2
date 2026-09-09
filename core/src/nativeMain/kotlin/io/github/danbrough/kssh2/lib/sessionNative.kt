package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.lib.LibSSH2.Session.waitSocket
import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_HEIGHT
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_HEIGHT_PX
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_WIDTH
import io.github.danbrough.libssh2.cinterop.LIBSSH2_TERM_WIDTH_PX
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_request_pty_ex
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.usePinned


fun nativeSessionRead(
  session: SessionPtr,
  socketHandle: SocketHandle,
  channelPtr: ChannelPtr,
  streamId: Int,
  buffer: ByteArray
): Long {
  buffer.usePinned { pinned ->

    // Get the raw pointer address of the specified offset in our byte array
    val targetAddress: CPointer<ByteVar> = pinned.addressOf(0)

    while (true) {
      // Call the actual C function directly like a regular Kotlin function!
      // libssh2_channel_read_ex returns a sign-extended long (ssize_t)
      val bytesRead:Long = libssh2_channel_read_ex(
        channel = channelPtr.toCPointer(),
        stream_id = streamId,
        buf = targetAddress,
        buflen = buffer.size.convert()
      )


      when {
        bytesRead > 0 -> {
          return bytesRead // Return read amount
        }

        bytesRead == 0L -> {
          return 0 // End of file (EOF)
        }

        bytesRead == LIBSSH2_ERROR_EAGAIN.toLong() -> {
          // Non-blocking catch: Yield control back to the coroutine dispatcher
          // instead of freezing the OS thread.
          //logNative.trace { "channelRead() libssh2_channel_read_ex() returned LIBSSH2_ERROR_EAGAIN" }
          waitSocket(session, socketHandle)
        }

        else -> {
          logNative.error {"Libssh2 read failed with native error code: $bytesRead" }
          return bytesRead.convert()
        }
      }
    }
    @Suppress("KotlinUnreachableCode")
    throw IllegalStateException("Unreachable code")
  }
}


fun nativeSessionRequestPty(channelPtr: ChannelPtr, terminal: String): Long {
  logNative.trace { "LibSSH2Native::Channel::requestPty() terminal:$terminal" }
  var rc = 0
  while (true) {
    rc = libssh2_channel_request_pty_ex(
      channelPtr.toCPointer(),
      terminal,
      terminal.length.convert(),
      null,
      0u,
      LIBSSH2_TERM_WIDTH.convert(),
      LIBSSH2_TERM_HEIGHT.convert(),
      LIBSSH2_TERM_WIDTH_PX.convert(),
      LIBSSH2_TERM_HEIGHT_PX.convert()
    )
    if (rc == LIBSSH2_ERROR_EAGAIN) {
      //TODO delay(10.milliseconds)
      continue
    }
    return rc.toLong()
  }
}