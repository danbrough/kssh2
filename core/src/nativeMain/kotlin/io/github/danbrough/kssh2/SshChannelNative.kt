package io.github.danbrough.kssh2

import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalForeignApi::class)
class SshChannelNative(
  // cinterop automatically generates 'LIBSSH2_CHANNEL' as an opaque type
  private val channelPointer: CPointer<LIBSSH2_CHANNEL>
) {

  /**
   * Reads data natively into a Kotlin ByteArray.
   * Non-blocking, handles LIBSSH2_ERROR_EAGAIN natively via Kotlin Coroutines.
   */
  suspend fun read(streamId: Int, buffer: ByteArray, len: Int = buffer.size): Int {
    if (len <= 0) return 0

    // Ensure execution happens on a background thread dispatcher
    return withContext(Dispatchers.IO) {

      // Pin the Kotlin array in memory so the Native Garbage Collector
      // doesn't move it while libssh2 is actively writing into it.
      buffer.usePinned { pinned ->

        // Get the raw pointer address of the specified offset in our byte array
        val targetAddress: CPointer<ByteVar> = pinned.addressOf(0)

        while (true) {
          // Call the actual C function directly like a regular Kotlin function!
          // libssh2_channel_read_ex returns a sign-extended long (ssize_t)
          val bytesRead = libssh2_channel_read_ex(
            channel = channelPointer,
            stream_id = streamId,
            buf = targetAddress,
            buflen = len.convert() // Automatically scales Int to size_t type safely
          )


          when {
            bytesRead > 0 -> {
              return@withContext bytesRead.toInt() // Return read amount
            }

            bytesRead == 0L -> {
              return@withContext 0 // End of file (EOF)
            }

            bytesRead == LIBSSH2_ERROR_EAGAIN.toLong() -> {
              // Non-blocking catch: Yield control back to the coroutine dispatcher
              // instead of freezing the OS thread.
              delay(10.milliseconds)
            }

            else -> {
              throw IllegalStateException("Libssh2 read failed with native error code: $bytesRead")
            }
          }
        }
        @Suppress("KotlinUnreachableCode")
        throw IllegalStateException("Unreachable code")
      }
    }
  }
}
