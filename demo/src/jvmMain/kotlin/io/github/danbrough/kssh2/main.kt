package io.github.danbrough.kssh2

import com.github.ajalt.mordant.rendering.TextColors
import com.sshtools.client.SshClient
import com.sshtools.client.SshClientContext
import com.sshtools.common.publickey.SshPrivateKeyFileFactory
import com.sshtools.common.publickey.SshPublicKeyFileFactory
import io.github.danbrough.katty.BasicCommandHandler
import io.github.danbrough.katty.KTerminal
import io.github.danbrough.katty.basicCommand
import io.github.danbrough.kssh2.lib.LibSSH2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.net.InetAddress


fun main(args: Array<String>) {
  demoLog.info { "LD_LIBRARY_PATH=${System.getenv("LD_LIBRARY_PATH")}" }
  demoLog.info { "DYLD_LIBRARY_PATH=${System.getenv("DYLD_LIBRARY_PATH")}" }
  demoLog.info { "MESSAGE=${System.getenv("MESSAGE")}" }
  demoLog.info { "SSH2_LIBS=${System.getenv("SSH2_LIBS")}" }

  val cmdHandler = BasicCommandHandler()
  cmdHandler.registerCommands(
    basicCommand("jniTest", "Tests that the JNI library works") {
      LibSSH2.initJNI()
    },
  )

  runBlocking {

    commonMain(args, cmdHandler)
  }
}


suspend fun KTerminal.sshTest(args: List<String>) {
  println("running ssh2 test with args: ${args.joinToString()}")

  val ssh = SshClient.SshClientBuilder.create().apply {
    withHost(InetAddress.getByName("192.168.0.4"))
    withUsername("dan")
    val privateKeyFile = File("/home/dan/.ssh/id_ed25519")
    val privateKey = SshPrivateKeyFileFactory.parse(privateKeyFile)
    println("private key: $privateKey")
    val publicKey =
      FileInputStream("/home/dan/.ssh/id_ed25519.pub").use { SshPublicKeyFileFactory.parse(it) }
    println("public key: $publicKey")
    //val passphrase = terminal.prompt("passphrase: ", hideInput = true) ?: return
    val passphrase = FileReader("/tmp/passphrase").use { it.readText().trim() }
    val keyPair = privateKey.toKeyPair(passphrase)

    println("keypair: $keyPair")
    //withIdentities(keyPair)
    //val p = PasswordAuthenticator.forPassword(passphrase)
    withSshContext(SshClientContext().setHostKeyVerification { string, key -> true })
    withPrivateKeyFile(privateKeyFile) { passphrase }

  }.build()

  println("connected: ${ssh.isConnected}")


  val channel = ssh.openSessionChannel()
  println("channel is connected: ${channel.isConnected}")
  val input = BufferedReader(InputStreamReader(channel.inputStream))
  val future =
    channel.executeCommand($$"echo The date at $HOSTNAME is `date` && ls ~/").waitForever()
  withContext(Dispatchers.IO) {
    input.readLines().forEach {
      println(TextColors.green(it))
    }
  }
  println("future: $future  done: ${future.isDone} success: ${future.isSuccess}")


}

