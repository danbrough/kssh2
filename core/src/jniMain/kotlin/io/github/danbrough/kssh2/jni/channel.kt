package io.github.danbrough.kssh2.jni

import io.github.danbrough.kssh2.LibSSH2
import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.jbyteArray
import platform.android.jclass
import platform.android.jint
import platform.android.jlong
import platform.android.jstring

@CName("${JNI_PREFIX}_00024Channel_channelOpen")
fun ssh2ChannelOpen(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  session: jlong,
  socket: jlong,
  channelType: jstring,
  windowSize: jint,
  packetSize: jint,
  message: jstring?
): jlong {
  val envPtr = env.pointed.pointed!!

  val channelTypePtr = envPtr.GetStringUTFChars!!(env, channelType, null)
  val channelTypeString = channelTypePtr?.toKString() ?: "session"

  val messagePtr = if (message != null) envPtr.GetStringUTFChars!!(env, message, null) else null
  val messageString = messagePtr?.toKString()

  val result = LibSSH2.Channel.channelOpen(
    session,
    socket,
    channelTypeString,
    windowSize,
    packetSize,
    messageString
  )

  envPtr.ReleaseStringUTFChars!!(env, channelType, channelTypePtr)
  if (message != null && messagePtr != null) envPtr.ReleaseStringUTFChars!!(
    env,
    message,
    messagePtr
  )

  return result
}

@CName("${JNI_PREFIX}_00024Channel_requestPty")
fun ssh2ChannelRequestPty(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  channel: jlong,
  terminalType: jstring
): jlong {
  val envPtr = env.pointed.pointed!!

  val terminalTypePtr = envPtr.GetStringUTFChars!!(env, terminalType, null)
  val terminalTypeString = terminalTypePtr?.toKString() ?: "vanilla"
  val result = LibSSH2.Channel.requestPty(channel, terminalTypeString)
  envPtr.ReleaseStringUTFChars!!(env, terminalType, terminalTypePtr)

  return result
}

//    actual external fun processStartup(sessionPtr: SessionPtr,socketHandle: SocketHandle,channel: ChannelPtr, request: String, message: String): Long
@CName("${JNI_PREFIX}_00024Channel_processStartup")
fun ssh2ChannelProcessStartup(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  sessionPtr: jlong,
  socketHandle: jlong,
  channelPtr: jlong,
  request: jstring,
  message: jstring
): jlong {
  val envPtr = env.pointed.pointed!!

  val requestPtr = envPtr.GetStringUTFChars!!(env, request, null)
  val messagePtr = envPtr.GetStringUTFChars!!(env, message, null)

  val ret = LibSSH2.Channel.processStartup(
    sessionPtr,
    socketHandle,
    channelPtr,
    requestPtr?.toKString()!!,
    messagePtr?.toKString()!!
  )

  envPtr.ReleaseStringUTFChars!!(env, request, requestPtr)
  envPtr.ReleaseStringUTFChars!!(env, message, messagePtr)
  return ret.convert()
}

@CName("${JNI_PREFIX}_00024Channel_write")
fun ssh2ChannelWrite(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  channel: jlong,
  data: jstring,
): jlong {
  val envPtr = env.pointed.pointed!!

  val dataPtr = envPtr.GetStringUTFChars!!(env, data, null)
  val dataString = dataPtr?.toKString()!!
  val ret = LibSSH2.Channel.write(channel, dataString)
  envPtr.ReleaseStringUTFChars!!(env, data, dataPtr)

  return ret
}

@CName("${JNI_PREFIX}_00024Channel_close")
fun ssh2ChannelClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  channel: jlong,
) = LibSSH2.Channel.close(channel)


/*
    actual external fun read(
      session: SessionPtr,
      socketHandle: SocketHandle,
      channelPtr: ChannelPtr,
      streamId: Int,
      buffer: ByteArray,
    ): Int
 */
@CName("${JNI_PREFIX}_00024Channel_read")
fun ssh2ChannelRead(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  sessionPtr: jlong,
  socketHandle: jlong,
  channelPtr: jlong,
  streamId: jint,
  buffer: jbyteArray
): jint {
  val envPtr = env.pointed.pointed!!
  val channel = channelPtr.toCPointer<LIBSSH2_CHANNEL>() ?: return -1

  val bufLen = envPtr.GetArrayLength!!(env, buffer)
  if (bufLen <= 0) return 0

  val elements = envPtr.GetByteArrayElements!!(env, buffer, null) ?: return -1

  var result: Int
  while (true) {
    result = libssh2_channel_read_ex(
      channel,
      streamId,
      elements.reinterpret(),
      bufLen.convert()
    ).toInt()

    if (result == LIBSSH2_ERROR_EAGAIN) {
      LibSSH2.Session.waitSocket(sessionPtr, socketHandle)
      continue
    }

    if (result <= 0)
      return result
    break
  }

  envPtr.ReleaseByteArrayElements!!(env, buffer, elements, 0)

  return result
}

