package io.github.danbrough.kssh2

import io.github.danbrough.katty.BasicCommandJob


val authTest: BasicCommandJob = { args ->
  parseArgs(args)?.run {
    ssh {
      session {
        demoLog.debug { "session scope started" }
        connect(host, port.toInt())
        demoLog.debug { "connected to ${host}:${port}" }

        demoLog.info { "trying password authentication to ${user}@${host} password: ${passphrase?.mapIndexed { index, ch -> if (index == 0) ch else '*' }?.joinToString("")}" }
        authenticatePassword(user,passphrase!!)
      }
    }
  }
}