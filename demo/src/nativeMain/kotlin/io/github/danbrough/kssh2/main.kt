package io.github.danbrough.kssh2

import io.github.danbrough.katty.BasicCommandHandler
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock




fun main(args: Array<String>) {
  println("ARGS: ${args.joinToString()}")
  val cmdHandler = BasicCommandHandler()

  cmdHandler.registerCommands(
    basicCommand("date", "prints the date") {
      println("Today is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}")
    },
    basicCommand("simpleExec", "simple exec test with pubkey authentication", KTerminal::simpleExec),
    basicCommand("simpleExecAgent", "simple exec test with ssh agent authentication", KTerminal::simpleExecAgent),
    basicCommand("shellDemo","simple shell demo", KTerminal::shellDemo),
  )

  runBlocking {
    commonMain(cmdHandler, args)
  }
}


