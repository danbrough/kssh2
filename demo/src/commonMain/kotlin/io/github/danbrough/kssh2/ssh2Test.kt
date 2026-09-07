package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.KTerminal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds


suspend fun KTerminal.ssh2Test(args: List<String>) {
  println("ssh2Test()")


  val config = parseArgs(args) ?: return
  println(TextColors.green("config: $config"))

  ssh {
    demoLog.debug { "ssh scope started.." }

    session {
      demoLog.debug { "session scope started" }
      connect(config.host, config.port.toInt())
      demoLog.debug { "connected to ${config.host}:${config.port}" }

      authenticateWithAgent(config.user)

      demoLog.debug { "authenticated with agent" }

      channel {

        withContext(Dispatchers.IO){
          demoLog.debug { "${SshUtils.threadName()}: opened channel" }
          exec("ls ~/")
          demoLog.debug { "${SshUtils.threadName()}: executed cmd.." }
          val flow = readChannel(32)
          demoLog.debug { "${SshUtils.threadName()}: got flow to read" }
          delay(1.seconds)
          demoLog.debug { "${SshUtils.threadName()}: collecting flow.." }
          delay(1.seconds)
          flow.collect {
            demoLog.debug { "${SshUtils.threadName()}: read: ${it.decodeToString()} " }
            delay(1.seconds)
          }
          demoLog.debug { "${SshUtils.threadName()}: finished collecting " }
        }

      }

    }
  }
}

