package io.github.danbrough.kssh2

import android.util.Log
import io.github.danbrough.kssh2.LibSSH2.Companion.testJNI
import org.danbrough.klog.klogFactory
import org.danbrough.klog.logger


fun messageTest() {
  Log.i("SSH2", "messageTest() from Log.i logFactory is $klogFactory message is $message")
  val log = logger("SSH2")



  log.info { "messageTest() the message is $message" }
  testJNI()

  log.info { "testing log .." }
  testLog()
}

external fun testLog()