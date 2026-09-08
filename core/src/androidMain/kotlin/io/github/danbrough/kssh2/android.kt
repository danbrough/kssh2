package io.github.danbrough.kssh2

import android.util.Log
import org.danbrough.klog.klogFactory
import org.danbrough.klog.logger

private val log = logger("SSH2")

suspend fun messageTest() {
  Log.i("SSH2", "messageTest() from Log.i logFactory is $klogFactory")
  log.info { "klog message" }

  runCatching {
    ssh {
      session {
        connect("192.168.0.2")
        authenticatePassword("fred", "ILikeCheese!")
        log.info { "authenticated" }
      }
    }
  }.exceptionOrNull()?.also {
    log.error { "ssh() failed $it" }
  }
}

external fun testLog()