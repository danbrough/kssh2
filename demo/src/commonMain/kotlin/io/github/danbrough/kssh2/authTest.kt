package io.github.danbrough.kssh2

import io.github.danbrough.katty.BasicCommandJob
import kotlinx.coroutines.flow.map


val authTest: BasicCommandJob = { args ->
  parseArgs(args)?.run {
    ssh {
      session {
        demoLog.debug { "session scope started" }
        connect(host, port.toInt())
        demoLog.debug { "connected to ${host}:${port}" }

        demoLog.info { "trying password authentication to ${user}@${host} password: ${passphrase?.mapIndexed { index, ch -> if (index == 0) ch else '*' }?.joinToString("")}" }
        authenticatePassword(user,passphrase!!)



        channel {
          demoLog.info { "created channel" }
          val cmd = $$"echo running $HOSTNAME at `date` ostype:$OSTYPE hosttype:$HOSTTYPE && ls ~/ && ([ -f /etc/os-release ] && cat /etc/os-release )"
          demoLog.info { "executing $cmd..." }
          exec(cmd)
          readChannel().map { it.decodeToString() }.collect {
            demoLog.debug { it }
          }
        }
      }
    }
  }
}