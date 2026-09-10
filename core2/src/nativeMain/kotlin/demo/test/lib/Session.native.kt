package demo.test.lib

import demo.test.ssh2.cinterops.LIBSSH2_SESSION
import demo.test.ssh2.cinterops.SSH_DISCONNECT_BY_APPLICATION
import demo.test.ssh2.cinterops.libssh2_session_disconnect_ex
import demo.test.ssh2.cinterops.libssh2_session_free
import demo.test.ssh2.cinterops.libssh2_session_init_ex
import demo.test.ssh2.cinterops.libssh2_session_set_blocking
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toLong
import platform.android.JNIEnvVar
import platform.android.JNI_TRUE
import platform.android.jboolean
import platform.android.jclass
import platform.android.jlong

actual object Session {
  actual fun createSession(blocking: Boolean): SessionPtr {
    println("LibSSH2Native::Session::createSession(blocking=$blocking)")
    val session: CPointer<LIBSSH2_SESSION> =
      libssh2_session_init_ex(null, null, null, null)
        ?: return 0

    libssh2_session_set_blocking(session, if (blocking) 1 else 0)
    return session.toLong()
  }

  actual fun close(session: SessionPtr) {
    session.toCPointer<LIBSSH2_SESSION>()?.also { sessionPtr ->
      println("LibSSH2Native::Session::closeSession()")
      libssh2_session_disconnect_ex(
        sessionPtr,
        SSH_DISCONNECT_BY_APPLICATION,
        "Normal Shutdown",
        ""
      )
      libssh2_session_free(sessionPtr)
    }
  }
}


@CName("${JNI_PREFIX}_Session_createSession")
fun ssh2CreateSession(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  blocking: jboolean
): jlong = Session.createSession(blocking.toInt() == JNI_TRUE)


@CName("${JNI_PREFIX}_Session_close")
fun ssh2CloseSession(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  session: jlong
) {
  Session.close(session)
}
