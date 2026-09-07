package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.KTerminal


suspend fun KTerminal.simpleExec(args: List<String>) {
  println("simpleExec()")
  val config = parseArgs(args) ?: return
  println(TextColors.green("config: $config"))

  ssh {
    sessionOld {
      connect(config.host, config.port.toInt())

      authenticatePubKey(
        config.user,
        privateKeyPath = config.privateKeyPath,
        password = config.passphrase
      )

      channel {
        exec("ls ~/")
        readLoop()
      }

      channel {
        exec($$"echo user = $USER date is `date` at $HOSTNAME")
        readLoop()
      }
    }
  }
}




