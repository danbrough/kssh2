package io.github.danbrough.kssh2

import io.github.danbrough.katty.basicCommand
import io.github.danbrough.kssh2.lib.onFailure
import io.github.danbrough.kssh2.lib.onSuccess
import io.github.danbrough.kssh2.lib.successOrThrow
import kotlinx.coroutines.flow.map
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString


val authTestPassword = basicCommand("authTestPassword", "Tests password authentication") { args ->
  parseArgs(args)?.run {
    ssh {
      session {
        demoLog.debug { "session scope started" }
        connect(host, port.toInt()).successOrThrow()
        demoLog.debug { "connected to ${host}:${port}" }

        demoLog.info {
          "trying password authentication to ${user}@${host} password: ${
            passphrase?.mapIndexed { index, ch -> if (index == 0) ch else '*' }?.joinToString("")
          }"
        }
        authenticatePassword(user, passphrase!!).onSuccess {
          runTestCommand()
        }.onFailure {
          demoLog.error { "authentication failed: error: ${code}:$message" }
        }
      }
    }
  }
}

suspend fun Session.runTestCommand() {
  channel {
    demoLog.info { "created channel" }
    val cmd =
      $$"echo running on $HOSTNAME at `date` ostype:$OSTYPE hosttype:$HOSTTYPE && ls ~/ && ( cat /etc/os-release 2> /dev/null )"
    demoLog.info { "executing $cmd..." }
    exec(cmd)
    buildString {
      readChannel().map { it.decodeToString() }.collect {
        append(it)
      }
    }.also {
      demoLog.debug { it }
    }
  }
}

val authTestPublicKey =
  basicCommand("authTestPublicKey", "Tests public-key authentication") { args ->

    parseArgs(args)?.also { config ->
      println("config: $config")
      ssh {
        session {
          demoLog.debug { "session scope started" }
          connect(config.host, config.port.toInt()).successOrThrow()
          demoLog.debug { "connected to ${config.host}:${config.port}" }


          authenticatePublicKey(
            config.user,
            config.pubKeyPath,
            config.privateKeyPath,
            config.passphrase
          ).onSuccess {
            demoLog.info { "authenticated" }
            runTestCommand()
          }.onFailure {
            demoLog.error { "authentication failed: $this" }
          }
        }
      }
    }
  }
