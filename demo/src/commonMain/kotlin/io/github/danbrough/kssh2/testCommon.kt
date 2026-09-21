package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.BasicCommandHandler
import io.github.danbrough.katty.DefaultHistory
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.KattyUtils
import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.files.Path
import org.danbrough.klog.logger
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal val demoLog = logger("SSH2DEMO")
private val log = demoLog

suspend fun newSSHScope() {
  ssh {
    log.debug { "newSSHScope::ssh scope: $this" }
    delay(1.seconds)
    log.debug { "newSSHScope::ssh finishing" }
  }
}

suspend fun commonMain(
  args: Array<String>,
  cmdHandler: BasicCommandHandler = BasicCommandHandler()
) {
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
    },
    basicCommand("sshScopeTest", "Testing ssh scope") {
      ssh {
        val ssh = currentCoroutineContext()[SSHScope] ?: error("no ssh scope")
        val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        log.debug { "${KattyUtils.threadName()} inside ssh scope: $this scope.message = ${ssh.message} time is $time" }
        ssh.message = time
        delay(1.seconds)
        log.debug { "starting new scope ..." }
        newSSHScope()
      }
    },
    scopeTest,
  )

  /**
   * Initialize the ssh library before we start.
   * Then it's close will only be called after the terminal has finished
   */


  log.debug { "commonMain() starting terminal .. thread: ${KattyUtils.threadName()}" }

  KTerminal(
    history = DefaultHistory(Path("./history.txt")),
    cmdContext = Dispatchers.Default,
    commandHandler = cmdHandler
  ).main(
    args
  )


}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun KTerminal.coroutineTest(array: List<String>) {
  log.debug { "coroutineTest: ${KattyUtils.threadName()}" }

  coroutineScope {
    withContext(Dispatchers.IO) {
      log.debug { "coroutineTEst:${KattyUtils.threadName()}  inside scope: $this" }
      delay(2.seconds)
      log.debug { "coroutineTEst:${KattyUtils.threadName()}  finishing inside scope: $this" }
    }
  }
  log.info { "coroutineTEst:${KattyUtils.threadName()}  outside scope: $this" }

  val channel = Channel<String>()
  coroutineScope {
    launch(Dispatchers.IO) {
      delay(1.seconds)
      for (n in 1..3) {
        val msg = "Message $n"
        log.trace { "sending $msg ${KattyUtils.threadName()}" }
        channel.send(msg)
        log.trace { "sent $msg ${KattyUtils.threadName()}" }
      }
    }

    log.debug { "got to here isEmpty: ${channel.isEmpty}" }
    delay(1.seconds)


    log.debug { "received: ${channel.receive()} ${KattyUtils.threadName()}" }
    log.debug { "received: ${channel.receive()} ${KattyUtils.threadName()}" }
    log.debug { "received: ${channel.receive()} ${KattyUtils.threadName()}" }
    log.debug { "received: isEmpty: ${channel.isEmpty} ${KattyUtils.threadName()}" }

    val flow = flow {
      var n = 0
      while (true) {
        n++
        val msg = "Message $n"
        log.trace { "sending $msg ${KattyUtils.threadName()}" }
        emit(msg)
        log.trace { "sent $msg ${KattyUtils.threadName()}" }
      }
    }.flowOn(Dispatchers.IO)
    log.debug { "created flow ..collecting .." }
    flow.take(10).collect {
      log.info { "collected $it on ${KattyUtils.threadName()}" }
    }

    log.warn { "finished" }

  }

}