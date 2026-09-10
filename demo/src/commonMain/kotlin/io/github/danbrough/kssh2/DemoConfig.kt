package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.KattyUtils
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

data class DemoConfig(
  val cmdName: String,
  var user: String = "",
  var host: String = "127.0.0.1",
  var port: String = "22",
  var pubKey: String = "",
  var privateKey: String = "",
  var passphrase: String? = null,
) {

  override fun toString(): String =
    "DemoConfig[user=$user,host=$host,port=$port,pubKeyPath=$pubKey,privateKeyPath=$privateKey,passphrase=${
      buildString {
        if (passphrase.isNullOrBlank()) append("null")
        else {
          append(passphrase!!.first())
          append("*".repeat(passphrase!!.length - 1))
        }
      }
    })]"

}


const val ENV_PREFIX = "SSHDEMO"
private val HELP_OPTIONS = setOf("-h", "?", "help", "--help")

fun KTerminal.printHelp(args: List<String>) {
  println("usage: ${args[0]} host=[server ip address] port=[server port] user=[remote user] publicKey=[public key string or path] privateKey=[private key string or path] passphrase=[passphrase or path to passphrase file]")
  println(" env variables ${ENV_PREFIX}_HOST=.. ${ENV_PREFIX}_PORT=.. ${ENV_PREFIX}_PUBLIC_KEY ... ${ENV_PREFIX}_PASSPHRASE=..")
}

suspend fun KTerminal.parseArgs(args: List<String>): DemoConfig? {
  val config = DemoConfig(args[0])

  if (args.any { it in HELP_OPTIONS }) {
    printHelp(args)
    return null
  }

  config.user = KattyUtils.getEnv("${ENV_PREFIX}_USER") ?: KattyUtils.getEnv("USER") ?: "user"
  config.host = KattyUtils.getEnv("${ENV_PREFIX}_HOST") ?: "127.0.0.1"
  config.port = KattyUtils.getEnv("${ENV_PREFIX}_PORT") ?: "22"
  config.pubKey = KattyUtils.getEnv("${ENV_PREFIX}_PUBLIC_KEY") ?: ""
  config.privateKey = KattyUtils.getEnv("${ENV_PREFIX}_PRIVATE_KEY") ?: ""
  config.passphrase = KattyUtils.getEnv("${ENV_PREFIX}_PASSPHRASE")

  val sshDir = Path(KattyUtils.getEnv("HOME") ?: ".", ".ssh")

  listOf("ed25519", "ecdsa", "rsa", "dsa").forEach { keyType ->
    val keyPath = Path(sshDir, "id_$keyType")
    if (SystemFileSystem.exists(keyPath)) {
      config.pubKey = Path(sshDir, "id_$keyType.pub").toString()
      config.privateKey = Path(sshDir, "id_$keyType").toString()
    }
  }

  args.drop(1).forEach {
    val (name, value) = it.split('=')
    when (name) {
      "user" -> config.user = value
      "host" -> config.host = value
      "port" -> config.port = value
      "publicKey" -> config.pubKey = value
      "privateKey" -> config.privateKey = value
      "passphrase" -> config.passphrase = value
      else -> {
        println((TextColors.brightRed + TextStyles.bold)("Invalid argument: $it"))
        printHelp(args)
      }
    }
  }

  config.passphrase?.also { passphrase ->
    val path = Path(passphrase)
    if (SystemFileSystem.exists(path)) {
      config.passphrase = SystemFileSystem.source(path).buffered().use {
        it.readString().trim()
      }
    }
  }

  if (!IPAddressValidator.isIPAddress(config.host)) {
    val hostNames = SshUtils.resolveHostName(config.host)
    println("hostNames: ${hostNames.joinToString(",")}")
    config.host = hostNames.firstOrNull() ?: config.host
  }

  return config
}
