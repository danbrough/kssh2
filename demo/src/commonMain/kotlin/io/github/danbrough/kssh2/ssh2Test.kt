package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.KattyUtils
import kotlinx.coroutines.flow.map


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
        demoLog.debug { "${KattyUtils.threadName()}: opened channel" }
        exec("ls ~/")
        demoLog.debug { "${KattyUtils.threadName()}: executed cmd.." }
        readChannel().map { it.decodeToString() }.collect {
          demoLog.debug { it }
        }
        demoLog.debug { "${KattyUtils.threadName()}: finished collecting " }
      }

    }
  }
}

