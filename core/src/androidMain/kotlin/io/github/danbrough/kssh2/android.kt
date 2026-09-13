package io.github.danbrough.kssh2

import android.util.Log
import io.github.danbrough.kssh2.lib.onFailure
import io.github.danbrough.kssh2.lib.onSuccess
import io.github.danbrough.kssh2.lib.successOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.danbrough.klog.klogFactory
import org.danbrough.klog.logger

private val log = logger("SSH2")

suspend fun Session.runTestCommand() {
  channel {
    log.info { "created channel" }
    val cmd =
      $$"echo running on $HOSTNAME at `date` ostype:$OSTYPE hosttype:$HOSTTYPE && ls ~/ && ( cat /etc/os-release 2> /dev/null )"
    log.info { "executing $cmd..." }
    exec(cmd)
    buildString {
      readChannel().map { it.decodeToString() }.collect {
        append(it)
      }
    }.also {
      log.debug { it }

    }
  }
}
suspend fun messageTest() = withContext(Dispatchers.IO) {
  Log.i("SSH2", "messageTest() from Log.i logFactory is $klogFactory")
  Log.v("SSH2","messageTest():: VERBOSE MESSAGE")
  Log.d("SSH2","messageTest():: DEBUG MESSAGE")
  Log.i("SSH2","messageTest():: INFO MESSAGE")
  Log.w("SSH2","messageTest():: WARN MESSAGE")
  Log.e("SSH2","messageTest():: ERROR MESSAGE")
  log.info { "running messageTest()" }

  val user = "fred"
  val host = "192.168.0.2"
  val port = "22"
  val passphrase = "ILikeCheese!"

  runCatching {
    ssh {
      session {
        log.debug { "session scope started" }
        connect(host, port.toInt()).successOrThrow()
        log.debug { "connected to ${host}:${port}" }

        log.info {
          "trying password authentication to ${user}@${host} password: ${
            passphrase.mapIndexed { index, ch -> if (index == 0) ch else '*' }.joinToString("")
          }"
        }
        authenticatePassword(user, passphrase).onSuccess {
          log.info { "authenticatePassword: success" }
          runTestCommand()
        }.onFailure {
          log.error { "authentication failed: error: ${code}:$message" }
        }
      }
    }
  }.exceptionOrNull()?.also {
    log.error { "ssh() failed $it" }
  }
}


external fun testLog()