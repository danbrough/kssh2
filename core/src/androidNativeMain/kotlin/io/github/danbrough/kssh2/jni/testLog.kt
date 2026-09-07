package io.github.danbrough.kssh2.jni

import platform.android.JNIEnvVar
import io.github.danbrough.kssh2.log
import kotlinx.cinterop.CPointer


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