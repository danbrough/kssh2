package io.github.danbrough.kssh2

import io.github.danbrough.katty.basicCommand
import io.github.danbrough.kssh2.lib.onFailure
import io.github.danbrough.kssh2.lib.onSuccess
import io.github.danbrough.kssh2.lib.successOrThrow
import kotlinx.coroutines.flow.map

private val log = demoLog

val authTestPassword = basicCommand("authTestPassword", "Tests password authentication") { args ->
  parseArgs(args)?.run {
    ssh {
      session {
        log.debug { "session scope started" }
        connect(host, port.toInt()).successOrThrow()
        log.debug { "connected to ${host}:${port}" }

        log.info {
          "trying password authentication to ${user}@${host} password: ${
            passphrase?.mapIndexed { index, ch -> if (index == 0) ch else '*' }?.joinToString("")
          }"
        }
        authenticatePassword(user, passphrase!!).onSuccess {
          runTestCommand()
        }.onFailure {
          log.error { "authentication failed: error: ${code}:$message" }
        }
      }
    }
  }
}

suspend fun Session.runTestCommand() {
  channel {
    log.info { "created channel" }
    val cmd =
      $$"echo running on $USER@$HOSTNAME at `date` ostype:$OSTYPE hosttype:$HOSTTYPE && ls ~/ && ( cat /etc/os-release 2> /dev/null )"
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

val authTestPublicKey =
  basicCommand("authTestPublicKey", "Tests public-key authentication") { args ->

    parseArgs(args)?.also { config ->
      println("config: $config")
      ssh {
        session {
          log.debug { "session scope started" }
          connect(config.host, config.port.toInt()).successOrThrow()
          log.debug { "connected to ${config.host}:${config.port}" }


          authenticatePublicKey(
            config.user,
            config.pubKey,
            config.privateKey,
            config.passphrase
          ).onSuccess {
            log.info { "authenticated" }
            runTestCommand()
          }.onFailure {
            log.error { "authentication failed: $this" }
          }
        }
      }
    }
  }


val authTestAgent =
  basicCommand("authTestAgent", "Tests agent authentication") { args ->

    parseArgs(args)?.also { config ->
      println("config: $config")
      ssh {
        session {
          log.debug { "session scope started" }
          connect(config.host, config.port.toInt()).successOrThrow()
          log.debug { "connected to ${config.host}:${config.port}" }

          authenticateWithAgent(config.user).onSuccess {
            log.info { "authenticated" }
            runTestCommand()
          }.onFailure {
            log.error { "authentication failed: $this" }
          }
        }
      }
    }
  }
