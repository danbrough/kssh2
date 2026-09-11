package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.lib.LibSSH2.Session
import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_channel_close
import io.github.danbrough.libssh2.cinterop.libssh2_channel_process_startup
import io.github.danbrough.libssh2.cinterop.libssh2_channel_write_ex
import kotlinx.cinterop.convert
import kotlinx.cinterop.toCPointer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual object LibChannel {

  actual fun channelOpen(
    session: SessionPtr,
    socket: SocketHandle,
    channelType: String,
    windowSize: Int,
    packetSize: Int,
    message: String?
  ): ChannelPtr = nativeChannelOpen(session, socket, channelType, windowSize, packetSize, message)

  /*
  LIBSSH2_API int libssh2_channel_request_pty_ex(LIBSSH2_CHANNEL *channel,
                                             const char *term,
                                             unsigned int term_len,
                                             const char *modes,
                                             unsigned int modes_len,
                                             int width, int height,
                                             int width_px, int height_px);
  #define libssh2_channel_request_pty(channel, term)                      \
  libssh2_channel_request_pty_ex((channel), (term),                   \
                                 (unsigned int)strlen(term),          \
                                 NULL, 0,                             \
                                 LIBSSH2_TERM_WIDTH,                  \
                                 LIBSSH2_TERM_HEIGHT,                 \
                                 LIBSSH2_TERM_WIDTH_PX,               \
                                 LIBSSH2_TERM_HEIGHT_PX)

   */
  actual fun requestPty(channelPtr: ChannelPtr, terminal: String): Long =
    nativeSessionRequestPty(channelPtr, terminal)

  /*
  #define libssh2_channel_exec(channel, command) \
    libssh2_channel_process_startup((channel), "exec", sizeof("exec") - 1, \
    (command), (unsigned int)strlen(command))


    #include <libssh2.h>

int
libssh2_channel_process_startup(LIBSSH2_CHANNEL *channel,
                              const char *request,
                              unsigned int request_len,
                              const char *message,
                              unsigned int message_len);
Description
channel - Active session channel instance.

request - Type of process to startup. The SSH2 protocol currently defines shell, exec, and subsystem as standard process services.

request_len - Length of request parameter.

message - Request specific message data to include.

message_len - Length of message parameter.

Initiate a request on a session type channel such as returned by libssh2_channel_open_ex(3).

Return value
Return 0 on success or negative on failure. It returns LIBSSH2_ERROR_EAGAIN when it would otherwise block. While LIBSSH2_ERROR_EAGAIN is a negative number, it is not really a failure per se.

Errors
LIBSSH2_ERROR_ALLOC - An internal memory allocation call failed.

LIBSSH2_ERROR_SOCKET_SEND - Unable to send data on socket.

LIBSSH2_ERROR_CHANNEL_REQUEST_DENIED -
   */

  actual fun processStartup(
    sessionPtr: SessionPtr,
    socketHandle: SocketHandle,
    channel: ChannelPtr,
    request: String,
    message: String
  ): Long {
    while (true) {
      val ret = libssh2_channel_process_startup(
        channel.toCPointer(),
        request,
        request.length.convert(),
        message,
        message.length.convert()
      )
      if (ret == LIBSSH2_ERROR_EAGAIN) {
        Session.waitSocket(sessionPtr, socketHandle)
        continue
      }
      return ret.toLong()
    }
  }


  actual fun close(channel: ChannelPtr) {
    logNative.trace { "LibSSH2Native::Channel::close()" }
    if (channel != 0L)
      libssh2_channel_close(channel.toCPointer())
  }

  actual fun write(channel: ChannelPtr, data: String): Long =
    libssh2_channel_write_ex(channel.toCPointer(), 0, data, data.length.convert())

  actual suspend fun readAll(channelPtr: ChannelPtr): Flow<ByteArray> = flow {
    val sshChannel = SshChannelNative(channelPtr.toCPointer()!!)
    val buf = ByteArray(1024)
    while (true) {
      val read = sshChannel.read(0, buf)
      if (read <= 0) break
      emit(buf.copyOfRange(0, read))
    }
  }

  actual fun read(
    session: SessionPtr,
    socketHandle: SocketHandle,
    channelPtr: ChannelPtr,
    streamId: Int,
    buffer: ByteArray
  ): Long = nativeSessionRead(session, socketHandle, channelPtr, streamId, buffer)
}