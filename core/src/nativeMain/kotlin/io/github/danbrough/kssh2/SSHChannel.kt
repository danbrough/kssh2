package io.github.danbrough.kssh2


import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL_PACKET_DEFAULT
import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL_WINDOW_DEFAULT
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_channel_close
import io.github.danbrough.libssh2.cinterop.libssh2_channel_get_exit_status
import io.github.danbrough.libssh2.cinterop.libssh2_channel_open_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_process_startup
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import io.github.danbrough.libssh2.cinterop.libssh2_channel_setenv_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_errno
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.yield

private val log = logNative

fun SSHSessionOld.channel(): SSHChannel {
  var channel: CPointer<LIBSSH2_CHANNEL>? = null

  val type = "session"

  var rc = LIBSSH2_ERROR_EAGAIN
  while (true) {
    channel = libssh2_channel_open_ex(
      session!!,
      type,
      type.length.convert(),
      LIBSSH2_CHANNEL_WINDOW_DEFAULT.toUInt(),
      LIBSSH2_CHANNEL_PACKET_DEFAULT.toUInt(),
      null,
      0.convert()
    )

    /*
        libssh2_channel_open_ex((session), "session", sizeof("session") - 1, \
                            LIBSSH2_CHANNEL_WINDOW_DEFAULT, \
                            LIBSSH2_CHANNEL_PACKET_DEFAULT, NULL, 0)

     */

    if (channel != null) break
    rc = libssh2_session_last_errno(session!!)
    if (rc != LIBSSH2_ERROR_EAGAIN) break
    waitSocket()
  }

  if (channel == null) error("libssh2_channel_open_ex(type=$type) -> $rc")

  return SSHChannel(this, channel)
}

suspend fun <R> SSHSessionOld.channel(block: suspend SSHChannel.() -> R) =
  sshScope(block, channel())

class SSHChannel(private val session: SSHSessionOld, val channel: CPointer<LIBSSH2_CHANNEL>) :
  Scope {

  fun exec(commandLine: String) {
    log.debug { "exec() $commandLine" }
    var rc: Int

    val processType = "exec" // "shell", "exec" or "subsystem"

    while (libssh2_channel_process_startup(
        channel,
        processType,
        processType.length.convert(),
        commandLine,
        commandLine.length.convert()
      ).also { rc = it } == LIBSSH2_ERROR_EAGAIN
    ) session.waitSocket()



    log.trace { "libssh2_channel_process_startup() returned $rc" }
    if (rc != 0) error("libssh2_channel_process_startup($commandLine) returned $rc")
  }


  /**
   *
   * Name
   * libssh2_channel_setenv_ex - set an environment variable on the channel
   *
   * Synopsis
   * #include <libssh2.h>
   *
   * int
   * libssh2_channel_setenv_ex(LIBSSH2_CHANNEL *channel,
   *                           char *varname, unsigned int varname_len,
   *                           const char *value, unsigned int value_len);
   *
   * int
   * libssh2_channel_setenv(LIBSSH2_CHANNEL *channel,
   *                        char *varname, const char *value);
   * Description
   * channel - Previously opened channel instance such as returned by libssh2_channel_open_ex(3)
   *
   * varname - Name of environment variable to set on the remote channel instance.
   *
   * varname_len - Length of passed varname parameter.
   *
   * value - Value to set varname to.
   *
   * value_len - Length of value parameter.
   *
   * Set an environment variable in the remote channel's process space. Note that this does not make sense for all channel types and may be ignored by the server despite returning success.
   *
   * Return value
   * Return 0 on success or negative on failure. It returns LIBSSH2_ERROR_EAGAIN when it would otherwise block. While LIBSSH2_ERROR_EAGAIN is a negative number, it is not really a failure per se.
   *
   * Errors
   * LIBSSH2_ERROR_ALLOC - An internal memory allocation call failed.
   *
   * LIBSSH2_ERROR_SOCKET_SEND - Unable to send data on socket.
   *
   * LIBSSH2_ERROR_CHANNEL_REQUEST_DENIED -
   */
  fun setEnvironment(name: String, value: String) {

    log.trace { "setEnvironment $name = $value" }
    val ret =
      libssh2_channel_setenv_ex(channel, name, name.length.toUInt(), value, value.length.toUInt())


    log.trace { "setEnvironment returned $ret  LIBSSH2_ERROR_EAGAIN == $LIBSSH2_ERROR_EAGAIN" }

  }


  fun shell() {
    log.debug { "shell()" }
    var rc: Int

    val processType = "shell" // "shell", "exec" or "subsystem"

    /*    rc = libssh2_channel_process_startup(
          channel,
          processType,
          processType.length.convert(),
          null,
          0.convert()
        )*/

    while (libssh2_channel_process_startup(
        channel,
        processType,
        processType.length.convert(),
        null, 0u
      ).also { rc = it } == LIBSSH2_ERROR_EAGAIN
    ) session.waitSocket()

    log.trace { "libssh2_channel_process_startup(shell) returned $rc" }
    if (rc != 0) error("libssh2_channel_process_startup(shell) returned $rc")


    /*    val shellType = "vanilla"

        while (libssh2_channel_request_pty_ex(
            channel, shellType, shellType.length.convert(), null, 0u,
            LIBSSH2_TERM_WIDTH, LIBSSH2_TERM_HEIGHT, LIBSSH2_TERM_WIDTH_PX, LIBSSH2_TERM_HEIGHT_PX
          ).also { rc = it } == LIBSSH2_ERROR_EAGAIN
        )
          session.waitSocket()

        if (rc != 0) error("libssh2_channel_request_pty_ex($shellType) returned $rc")*/

  }


  var cancel = false
  suspend fun readLoop() {
    val buffer = ByteArray(1024) //allocArray<ByteVar>(0x4000)
    memScoped {

      var readCount = 0L


      while (true) {
        do {

          buffer.usePinned {

            log.trace { "calling libssh2_channel_read_ex.." }
            readCount =
              libssh2_channel_read_ex(channel, 0, it.addressOf(0), buffer.size.convert())
            if (readCount > 0) {
              log.info {
                "readCount: $readCount <${
                  buffer.decodeToString(
                    0,
                    readCount.convert()
                  )
                }>"
              }
            } else {
              if (readCount != LIBSSH2_ERROR_EAGAIN.toLong() && readCount != 0L)
                error("libssh2_channel_read_ex returned $readCount")
            }
          }

        } while (readCount > 0L)

        if (cancel) {
          cancel = false
          break
        }
        if (readCount == LIBSSH2_ERROR_EAGAIN.toLong()) {
          log.trace { "readCount: $readCount" }
          yield()
          session.waitSocket()
        } else break
      }
    }
  }

  override fun close() {
    log.trace { "Channel::close()" }

    var rc: Int
    while (libssh2_channel_close(channel).also { rc = it } == LIBSSH2_ERROR_EAGAIN)
      session.waitSocket()
    log.trace { "libssh2_channel_close() -> $rc" }

    if (rc == 0) {
      libssh2_channel_get_exit_status(channel).also {
        log.trace { "libssh2_channel_get_exit_status(channel) -> $it" }
      }
    }

  }

}