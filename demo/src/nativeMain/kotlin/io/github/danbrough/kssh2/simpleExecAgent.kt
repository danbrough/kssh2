package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.libssh2.cinterop.LIBSSH2_ERROR_EAGAIN
import io.github.danbrough.libssh2.cinterop.libssh2_agent_connect
import io.github.danbrough.libssh2.cinterop.libssh2_agent_get_identity
import io.github.danbrough.libssh2.cinterop.libssh2_agent_init
import io.github.danbrough.libssh2.cinterop.libssh2_agent_list_identities
import io.github.danbrough.libssh2.cinterop.libssh2_agent_publickey
import io.github.danbrough.libssh2.cinterop.libssh2_agent_userauth
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value

suspend fun KTerminal.simpleExecAgent(args: List<String>) {

  println("simpleExecAgent()")
  val config = parseArgs(args) ?: return
  println(TextColors.green("config: $config"))

  ssh {
    sessionOld {
      memScoped {
        connect(config.host,config.port.toInt())

        demoLog.trace { "session: $session" }
        val agent: CPointer<cnames.structs._LIBSSH2_AGENT> =
          libssh2_agent_init(session) ?: error("libssh2_agent_init() failed")
        demoLog.trace { "got agent: $agent" }
        if (libssh2_agent_connect(agent) != 0)
          error("libssh2_agent_connect() failed")
        demoLog.debug { "connected to agent" }

        libssh2_agent_list_identities(agent).also {
          if (it != 0) error("libssh2_agent_list_identities failed err: $it")
        }

        val identityVar = alloc<CPointerVar<libssh2_agent_publickey>>()
        identityVar.value = null


        var prev: CPointer<libssh2_agent_publickey>? = null
        var success = false
        while (libssh2_agent_get_identity(agent, identityVar.ptr, prev) == 0) {
          demoLog.trace { "trying to authenticate with identity: ${identityVar.pointed?.comment?.toKString()}" }
          // 4. Try authenticating with the current identity
          libssh2_agent_userauth(agent, config.user, identityVar.value).also {
            if (it == 0) {
              println("Authentication successful!")
              success = true
              break
            } else {
              println("Failed err: $it  LIBSSH2_ERROR_EAGAIN = $LIBSSH2_ERROR_EAGAIN")
              if (it == LIBSSH2_ERROR_EAGAIN) {
                waitSocket()
                continue
              }
            }
          }

          // 5. Update prev for the next iteration step
          prev = identityVar.value
        }

        println("finished. success: $success")

        if (success) {
          channel {
            exec($$"echo The date at $USER:$HOSTNAME is `date` && ls -al ~/")
            readLoop()
          }
        }
      }
    }

  }


}

