package io.github.danbrough.kssh2.lib

import io.github.danbrough.libssh2.cinterop.libssh2_session_last_error
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.android.JNIEnvVar
import platform.android.JNI_TRUE
import platform.android.jboolean
import platform.android.jclass
import platform.android.jint
import platform.android.jlong
import platform.android.jstring

private const val JNI_PREFIX = "${JNI_PREFIX_PACKAGE}_LibSession"

@CName("${JNI_PREFIX}_createSession")
fun ssh2CreateSession(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  blocking: jboolean
): jlong = LibSession.createSession(blocking.toInt() == JNI_TRUE)


@CName("${JNI_PREFIX}_close")
fun ssh2SessionClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptr: jlong
) = LibSession.close(ptr)

@CName("${JNI_PREFIX}_sessionHandshake")
fun ssh2SessionHandshake(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptrSession: jlong,
  ptrSocket: jlong,
) = LibSession.sessionHandshake(ptrSession, ptrSocket)


@CName("${JNI_PREFIX}_waitSocket")
fun ssh2SessionWaitsocket(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  ptrSession: jlong,
  ptrSocket: jlong,
): jlong = LibSession.waitSocket(ptrSession, ptrSocket)


@CName("${JNI_PREFIX}_authenticateWithAgent")
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
  val result = LibSession.authenticateWithAgent(session, socket, remoteUserString)
  envPtr.ReleaseStringUTFChars!!(env, remoteUser, remoteUserPtr)
  return result
}




@CName("${JNI_PREFIX}_authenticatePassword")
fun ssh2SessionAuthenticateWithPassword(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  session: jlong,
  socket: jlong,
  remoteUser: jstring,
  password: jstring
): jint = env.jniEnv {
  jString(remoteUser) { user ->
    jString(password) { password ->
      LibSession.authenticatePassword(session, socket, user!!, password!!)
    }
  }
}


@CName("${JNI_PREFIX}_getErrorJNI")
fun getSessionError(
  env: CPointer<JNIEnvVar>,
  clz: jclass,
  sessionPtrValue: jlong
): jstring? = memScoped {
  val errmsgVar = alloc<CPointerVar<ByteVar>>()
  val errmsgLenVar = alloc<IntVar>()

  libssh2_session_last_error(
    sessionPtrValue.toCPointer(),
    errmsgVar.ptr,
    errmsgLenVar.ptr,
    0
  )

  val nativeMessage: CPointer<ByteVar>? = errmsgVar.value

  val pointee = env.pointed
  val functions = pointee.pointed ?: return null
  val newStringUTF = functions.NewStringUTF ?: return null

  return newStringUTF(env, nativeMessage)
}

@CName("${JNI_PREFIX}_authenticatePublicKey")
fun sessionAuthenticatePublicKey(
  env: CPointer<JNIEnvVar>,
  clz: jclass,
  sessionPtrValue: jlong,
  socket: jlong,
  user: jstring,
  publicKeyData: jstring,
  privateKeyData: jstring,
  password: jstring
): jint = env.jniEnv {
  jString(
    user,
    publicKeyData,
    privateKeyData,
    password
  ) { user, publicKeyData, privateKeyData, password ->
    LibSession.authenticatePublicKey(
      sessionPtrValue,
      socket,
      user,
      publicKeyData,
      privateKeyData,
      password
    )
  }
}




