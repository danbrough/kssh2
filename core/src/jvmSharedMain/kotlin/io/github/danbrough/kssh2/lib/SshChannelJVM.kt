package io.github.danbrough.kssh2.lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import kotlin.time.Duration.Companion.milliseconds

class SshChannelJVM(private val channelPointer: Long) {

  // Internal JNI method using a Direct ByteBuffer


  private external fun sshChannelRead(
    channelPtr: Long,
    streamId: Int,
    buffer: ByteBuffer,
    position: Int,
    remaining: Int
  ): Int


  /**
   * Reads data directly into a Direct ByteBuffer without copying memory.
   * Automatically handles non-blocking LIBSSH2_ERROR_EAGAIN retries.
   */
  suspend fun read(streamId: Int, buffer: ByteBuffer): Int {
    require(buffer.isDirect) { "Buffer must be allocated via ByteBuffer.allocateDirect()" }

    return withContext(Dispatchers.IO) {
      while (true) {
        val position = buffer.position()
        val remaining = buffer.remaining()


        if (remaining <= 0) return@withContext 0

        // Perform the native read operation
        val result = sshChannelRead(channelPointer, streamId, buffer, position, remaining)

        when {
          result > 0 -> {
            // Success: Advance the buffer position manually to match what C++ read
            buffer.position(position + result)
            return@withContext result
          }

          result == 0 -> {
            // End of file (EOF)
            return@withContext 0
          }

          result == -37 -> { // -37 is LIBSSH2_ERROR_EAGAIN
            // Libssh2 needs more time. Back off briefly to prevent CPU spinning, then loop.
            delay(10.milliseconds)
          }

          else -> {
            // Any other negative value represents a severe internal libssh2 error
            throw IllegalStateException("SSH read error code: $result")
          }
        }
      }
      throw IllegalStateException("Unreachable code") // Needed to satisfy compiler type checks
    }
  }
}