@file:Suppress("unused")

package io.github.danbrough.kssh2.jni

import io.github.danbrough.libssh2.cinterop.LIBSSH2_CHANNEL
import io.github.danbrough.libssh2.cinterop.LIBSSH2_SESSION
import io.github.danbrough.libssh2.cinterop.libssh2_channel_read_ex
import io.github.danbrough.libssh2.cinterop.libssh2_session_last_error
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.value
import platform.android.JNIEnvVar
import platform.android.jint
import platform.android.jlong
import platform.android.jobject
import platform.android.jstring

/*
extern "C" JNIEXPORT jint JNICALL
Java_com_example_SshChannel_nativeReadDirect(
    JNIEnv *env,
    jobject thiz,
    jlong channel_ptr,
    jint stream_id,
    jobject buffer,
    jint position,
    jint remaining
) {
    // 1. Restore the channel pointer
    LIBSSH2_CHANNEL* channel = reinterpret_cast<LIBSSH2_CHANNEL*>(channel_ptr);
    if (!channel) return -1; // Standard error fallback

    // 2. Fetch the memory address of the Direct ByteBuffer natively
    char* buffer_address = static_cast<char*>(env->GetDirectBufferAddress(buffer));
    if (!buffer_address) return -1;

    // Calculate the memory writing entry point based on the current position offset
    char* target_ptr = buffer_address + position;

    // 3. Make the libssh2 call directly into JVM-allocated direct memory
    // If session is non-blocking, this safely outputs LIBSSH2_ERROR_EAGAIN (-37)
    ssize_t result = libssh2_channel_read_ex(channel, stream_id, target_ptr, static_cast<size_t>(remaining));

    return static_cast<jint>(result);
}
 */

@CName("Java_io_github_danbrough_kssh2_SshChannelJVM_sshChannelRead")
fun sshChannelRead(
  env: CPointer<JNIEnvVar>,
  thiz: jobject,
  channelPtr: jlong,
  streamId: jint,
  buffer: jobject,
  position: jint,
  remaining: jint
): jint {
  val envPtr = env.pointed.pointed!!

  log.debug { "sshChannelRead()" }
  // 1. Restore the channel pointer
  val channel = channelPtr.toCPointer<LIBSSH2_CHANNEL>() ?: return -1

  // 2. Fetch the memory address of the Direct ByteBuffer natively


  val bufferAddress = envPtr.GetDirectBufferAddress!!.invoke(env, buffer) ?: return -1
  val targetPtr = bufferAddress.reinterpret<ByteVar>() + position

  // 3. Make the libssh2 call directly into JVM-allocated direct memory
  val result = libssh2_channel_read_ex(
    channel,
    streamId,
    targetPtr,
    remaining.toULong()
  )

  return result.toInt()
}


