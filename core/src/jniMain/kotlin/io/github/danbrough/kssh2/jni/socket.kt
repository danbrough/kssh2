package io.github.danbrough.kssh2.jni

import io.github.danbrough.kssh2.LibSSH2
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jint
import platform.android.jlong
import platform.android.jstring


@CName("${JNI_PREFIX}_00024Socket_connect")
fun ssh2SocketConnect(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  hostName: jstring,
  port: jint
): jlong {

  val envPtr = env.pointed.pointed!!

  // Convert jstring to Kotlin String
  val hostNamePtr = envPtr.GetStringUTFChars!!(env, hostName, null)
  val hostNameString = hostNamePtr?.toKString() ?: ""
  //log.trace { "ssh2SocketConnect(): hostName:$hostNameString port:$port" }

  // Call the actual native implementation
  val result = LibSSH2.Socket.connect(hostNameString, port)

  // Clean up
  envPtr.ReleaseStringUTFChars!!(env, hostName, hostNamePtr)

  return result
}

@CName("${JNI_PREFIX}_00024Socket_close")
fun ssh2SocketClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  socket: jlong
) = LibSSH2.Socket.close(socket)