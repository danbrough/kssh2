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
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path
import org.danbrough.klog.logger
import kotlin.time.Duration.Companion.seconds

internal val demoLog = logger("SSH2DEMO")
private val log = demoLog

suspend fun commonMain(cmdHandler: BasicCommandHandler, args: Array<String>) {
  cmdHandler.registerCommands(
    basicCommand("ssh2Test", "Runs some tests in LibSSH2", KTerminal::ssh2Test),
    authTestPassword,
    authTestPublicKey,
    authTestAgent,
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
  log.debug { "coroutineTest: ${SshUtils.threadName()}" }

  coroutineScope {
    withContext(Dispatchers.IO) {
      log.debug { "coroutineTEst:${SshUtils.threadName()}  inside scope: $this" }
      delay(2.seconds)
      log.debug { "coroutineTEst:${SshUtils.threadName()}  finishing inside scope: $this" }
    }
  }
  log.info { "coroutineTEst:${SshUtils.threadName()}  outside scope: $this" }

  val channel = Channel<String>()
  coroutineScope {
    launch(Dispatchers.IO) {
      delay(1.seconds)
      for (n in 1..3) {
        val msg = "Message $n"
        log.trace { "sending $msg ${SshUtils.threadName()}" }
        channel.send(msg)
        log.trace { "sent $msg ${SshUtils.threadName()}" }
      }
    }

    log.debug { "got to here isEmpty: ${channel.isEmpty}" }
    delay(1.seconds)


    log.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    log.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    log.debug { "received: ${channel.receive()} ${SshUtils.threadName()}" }
    log.debug { "received: isEmpty: ${channel.isEmpty} ${SshUtils.threadName()}" }

    val flow = flow {
      var n = 0
      while (true) {
        n++
        val msg = "Message $n"
        log.trace { "sending $msg ${SshUtils.threadName()}" }
        emit(msg)
        log.trace { "sent $msg ${SshUtils.threadName()}" }
      }
    }.flowOn(Dispatchers.IO)
    log.debug { "created flow ..collecting .." }
    flow.take(10).collect {
      log.info { "collected $it on ${SshUtils.threadName()}" }
    }

    log.warn { "finished" }

  }

}