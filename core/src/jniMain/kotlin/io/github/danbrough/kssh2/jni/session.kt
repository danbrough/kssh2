package io.github.danbrough.kssh2.jni

import io.github.danbrough.kssh2.LibSSH2
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.JNI_TRUE
import platform.android.jboolean
import platform.android.jclass
import platform.android.jlong
import platform.android.jstring


@CName("${JNI_PREFIX}_00024Session_createSession")
fun ssh2CreateSession(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  blocking: jboolean
): jlong = LibSSH2.Session.createSession(blocking.toInt() == JNI_TRUE)


@CName("${JNI_PREFIX}_00024Session_close")
fun ssh2SessionClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptr: jlong
) = LibSSH2.Session.close(ptr)

@CName("${JNI_PREFIX}_00024Session_sessionHandshake")
fun ssh2SessionHandshake(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptrSession: jlong,
  ptrSocket: jlong,
) = LibSSH2.Session.sessionHandshake(ptrSession, ptrSocket)


@CName("${JNI_PREFIX}_00024Session_waitSocket")
fun ssh2SessionWaitsocket(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptrSession: jlong,
  ptrSocket: jlong,
): jlong = LibSSH2.Session.waitSocket(ptrSession, ptrSocket)



@CName("${JNI_PREFIX}_00024Session_authenticateWithAgent")
fun ssh2SessionAuthenticateWithAgent(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  session: jlong,
  socket: jlong,
  remoteUser: jstring
): jlong {
  val envPtr = env.pointed.pointed!!
  val remoteUserPtr = envPtr.GetStringUTFChars!!(env, remoteUser, null)
  val remoteUserString = remoteUserPtr?.toKString() ?: ""
  val result = LibSSH2.Session.authenticateWithAgent(session, socket, remoteUserString)
  envPtr.ReleaseStringUTFChars!!(env, remoteUser, remoteUserPtr)
  return result
}