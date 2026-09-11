package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.logNative
import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_channel_open_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_errno
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toLong

/*
#define libssh2_channel_open_session(session) \
libssh2_channel_open_ex((session), "session", sizeof("session") - 1, \
                    LIBSSH2_CHANNEL_WINDOW_DEFAULT, \
                    LIBSSH2_CHANNEL_PACKET_DEFAULT, NULL, 0)
#include <libssh2.h>

LIBSSH2_CHANNEL *
libssh2_channel_open_ex(LIBSSH2_SESSION *session, const char *channel_type,
                    unsigned int channel_type_len,
                    unsigned int window_size,
                    unsigned int packet_size,
                    const char *message, unsigned int message_len);

LIBSSH2_CHANNEL *
libssh2_channel_open_session(session);
*/
fun nativeChannelOpen(
  session: SessionPtr,
  socket: SocketHandle,
  channelType: String,
  windowSize: Int,
  packetSize: Int,
  message: String?
): ChannelPtr {

  logNative.debug { "channelOpen() session:$session socket:$socket channel type:$channelType windowSize:$windowSize packetSize:$packetSize" }

  var channel: CPointer<LIBSSH2_CHANNEL>? = null
  var rc = LIBSSH2_ERROR_EAGAIN
  while (true) {
    channel = libssh2_channel_open_ex(
      session.toCPointer(),
      channelType,
      channelType.length.convert(),
      windowSize.convert(),
      packetSize.convert(),
      message,
      message?.length?.convert() ?: 0u
    )

    if (channel != null) break
    logNative.trace { "channelOpen() libssh2_channel_open_ex() returned" }
    rc = libssh2_session_last_errno(session.toCPointer())
    logNative.trace { "channelOpen() libssh2_channel_open_ex() rc = $rc" }
    if (rc != LIBSSH2_ERROR_EAGAIN) break
    LibSession.waitSocket(session, socket)
  }

  if (channel == null) error("libssh2_channel_open_ex(channelType=$channelType) -> $rc")
  return channel.toLong()
}