package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.libssh2.cinterop.libssh2_channel_write_ex
import kotlinx.cinterop.convert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

suspend fun KTerminal.shellDemo(args: List<String>) {
  println("shellDemo()")
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

        coroutineScope {

          demoLog.info { "running shell()" }
          shell()
          demoLog.trace { "shell() returned .. calling date" }
          var cmd = "date\n"
          libssh2_channel_write_ex(channel, 0, cmd, cmd.length.convert()).also {
            demoLog.info { "libssh2_channel_write_ex [$cmd] returned $it" }
          }

          val readJob = launch(Dispatchers.IO) {
            demoLog.warn { "starting readLoop.." }
            delay(1.seconds)
            readLoop()
            demoLog.warn { "readloop finished" }
          }

          demoLog.info { "read launch done" }
          delay(2.seconds)
          demoLog.warn { "calling date() again .." }

          cmd =
            $$"echo user:$USER hostname:$HOSTNAME hosttype: $HOSTTYPE ostype: $OSTYPE uptime: `uptime`\n"
          libssh2_channel_write_ex(channel, 0, cmd, cmd.length.convert()).also {
            demoLog.info { "libssh2_channel_write_ex [$cmd] returned $it" }
          }



          cmd = "MESSAGE=\"HELLO_WORLD at `date`\"\n"
          libssh2_channel_write_ex(channel, 0, cmd, cmd.length.convert()).also {
            demoLog.info { "libssh2_channel_write_ex [$cmd] returned $it" }
          }


          cmd = $$"echo The message is $MESSAGE\n"
          libssh2_channel_write_ex(channel, 0, cmd, cmd.length.convert()).also {
            demoLog.info { "libssh2_channel_write_ex [$cmd] returned $it" }
          }


          delay(4.seconds)
          demoLog.warn { "cancelling.." }
          readJob.cancel()
        }
        demoLog.error { "at the end" }
      }

    }
  }
}



