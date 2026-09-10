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




@CName("${JNI_PREFIX}_00024Session_authenticatePassword")
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
      LibSSH2.Session.authenticatePassword(session, socket, user!!, password!!)
    }
  }
}


@CName("${JNI_PREFIX}_00024Session_getErrorJNI")
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

@CName("${JNI_PREFIX}_00024Session_authenticatePublicKey")
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
    LibSSH2.Session.authenticatePublicKey(
      sessionPtrValue,
      socket,
      user,
      publicKeyData,
      privateKeyData,
      password
    )
  }
}




