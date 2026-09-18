# SSH2 Kotlin Multiplatform Integration Demo

Install [libssh2](https://github.com/libssh2/libssh2) or compile it for your system or android
using the [scripts](./scripts/build-all.sh) and then check that [ssh2_template.def](core/src/cinterop/ssh2_template.def) is suitable for your build environment.



Starting point: [sshdemo](./demo/sshdemo) to run the native demo.

There is also a java based demo [jsshdemo](./demo/jsshdemo) using JNI.

Check that [ssh2.def](core/src/cinterop/libssh2.def) is suitable for your build environment.


## Code example

```kotlin

runBlocking {
  parseArgs(args)?.also { config ->
    println("config: $config")
    ssh {
      session {
        log.debug { "session scope started" }
        connect(config.host, config.port.toInt()).successOrThrow()
        log.debug { "connected to ${config.host}:${config.port}" }

        authenticateWithAgent(config.user).onSuccess {
          log.info { "authenticated" }
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
              log.info { toString() }
            }
          }
        }.onFailure {
          log.error { "authentication failed: $this" }
        }
      }
    }
  }
}
```

