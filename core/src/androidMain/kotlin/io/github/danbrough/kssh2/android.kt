package io.github.danbrough.kssh2

import android.util.Log
import org.danbrough.klog.klogFactory
import org.danbrough.klog.logger

private val log = logger("SSH2")

suspend fun messageTest() {
  Log.i("SSH2", "messageTest() from Log.i logFactory is $klogFactory")
  log.info { "klog message" }

  ssh {
    session {

    }
  }
}

external fun testLog()