package io.github.danbrough.kssh2.jni

import platform.android.JNIEnvVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.danbrough.klog.logger
import platform.android.ANDROID_LOG_FATAL
import platform.android.__android_log_print
import platform.posix.abort
import platform.posix.memset


private val log = logger("SSH2")

@CName("Java_io_github_danbrough_kssh2_AndroidKt_testLog")
fun testLog(env: CPointer<JNIEnvVar>) {
  log.warn { "testLog(): running!!!!!!!!!!" }
  log.trace { "klog verbose" }
  log.debug { "klog debug" }
  log.info { "klog info" }
  log.warn { "klog warn" }
  log.error { "klog error" }

  log.info { "well that went well I thought." }


/*  __android_log_print(
    ANDROID_LOG_WARN,
    "SSH2",
    "testSomething() from Log.i logFactory is $klogFactory"
  )*/
}

@CName("__assert_fail")
fun assertFailHook(
  assertion: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>?,
  file: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>?,
  line: Int,
  function: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>?
) {
  val assertionStr = assertion?.toKString() ?: "unknown"
  val fileStr = file?.toKString() ?: "unknown"
  val functionStr = function?.toKString() ?: "unknown"

  __android_log_print(
    ANDROID_LOG_FATAL.toInt(),
    "KN_ASSERT_SHIM",
    "Assertion '%s' failed at %s:%d in function %s",
    assertionStr, fileStr, line, functionStr
  )

  abort()
}


@CName("explicit_bzero")
fun explicitBzeroHook(s: kotlinx.cinterop.CPointer<out kotlinx.cinterop.CPointed>?, n: Long) {
  if (s != null && n > 0) {
    // Fall back to a standard memset to clear the memory buffer safely
    memset(s, 0, n.toULong())
  }
}