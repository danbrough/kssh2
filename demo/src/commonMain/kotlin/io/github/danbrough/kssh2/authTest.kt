package io.github.danbrough.kssh2

import io.github.danbrough.katty.BasicCommandJob
import io.github.danbrough.kssh2.lib.successOrThrow
import kotlinx.coroutines.flow.map


val authTest: BasicCommandJob = { args ->
  parseArgs(args)?.run {
    ssh {
      session {
        demoLog.debug { "session scope started" }
        connect(host, port.toInt()).successOrThrow()
        demoLog.debug { "connected to ${host}:${port}" }

        demoLog.info { "trying password authentication to ${user}@${host} password: ${passphrase?.mapIndexed { index, ch -> if (index == 0) ch else '*' }?.joinToString("")}" }
        authenticatePassword(user,passphrase!!).successOrThrow()



        channel {
          demoLog.info { "created channel" }
          val cmd = $$"echo running on $HOSTNAME at `date` ostype:$OSTYPE hosttype:$HOSTTYPE && ls ~/ && ( cat /etc/os-release 2> /dev/null )"
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
    }
  }
}