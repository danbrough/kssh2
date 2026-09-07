package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.BasicCommandHandler
import io.github.danbrough.katty.DefaultHistory
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import org.danbrough.klog.logger
import kotlin.time.Duration.Companion.seconds

internal val demoLog = logger("SSH2DEMO")

suspend fun commonMain(cmdHandler: BasicCommandHandler, args: Array<String>) {
  cmdHandler.registerCommands(
    basicCommand("ssh2Test", "Runs some tests in LibSSH2", KTerminal::ssh2Test),

    basicCommand("configTest", "Prints the config") { args ->
      println("configTest()")
      parseArgs(args)?.also {
        println(TextColors.green("config: $it"))
      }
    },
    basicCommand("coroutineTest", "testing stuff") {
      coroutineTest(it)
    }
  )
  val terminal =
    KTerminal(history = DefaultHistory(Path("./history.txt")), commandHandler = cmdHandler)
  terminal.main(args)
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
suspend fun KTerminal.coroutineTest(array: List<String>) {
  demoLog.debug { "coroutineTest: ${SshUtils.threadName()}" }
  val channel = Channel<String>()
  coroutineScope {
    launch(Dispatchers.IO) {
      delay(1.seconds)
      for (n in 1..3) {
        val msg = "Message $n"
        demoLog.trace { "sending $msg ${SshUtils.threadName()}" }
        channel.send(msg)
        demoLog.trace { "sent $msg ${SshUtils.threadName()}" }
      }
    }

    demoLog.debug { "got to here isEmpty: ${channel.isEmpty}" }
    delay(1.seconds)


    demoLog.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    demoLog.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    demoLog.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    demoLog.debug { "received: isEmpty: ${channel.isEmpty} ${SshUtils.threadName()}" }

    val flow = flow {
      var n = 0
      while (true) {
        n++
        val msg = "Message $n"
        demoLog.trace { "sending $msg ${SshUtils.threadName()}" }
        emit(msg)
        demoLog.trace { "sent $msg ${SshUtils.threadName()}" }
      }
    }.flowOn(Dispatchers.IO)
    demoLog.debug { "created flow ..collecting .." }
    flow.take(10).collect {
      demoLog.info { "collected $it on ${SshUtils.threadName()}" }
    }

    demoLog.warn { "finished" }

  }

}