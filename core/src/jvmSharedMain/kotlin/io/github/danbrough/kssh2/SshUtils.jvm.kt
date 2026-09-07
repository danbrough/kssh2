package io.github.danbrough.kssh2

import java.net.Inet4Address
import java.net.Inet6Address

actual object SshUtils {
  actual fun getEnv(name: String): String? = System.getenv(name)
  actual fun threadName(): String = Thread.currentThread().name
  actual fun resolveHostName(hostName: String): List<String> {

    if (IPAddressValidator.isIPAddress(hostName)) return listOf(hostName)
    val addresses = mutableListOf<String>()

    runCatching {
      addresses.addAll(Inet4Address.getAllByName(hostName).toList().map { it.hostAddress })
    }
    runCatching {
      Inet6Address.getAllByName(hostName).toList().map { it.hostAddress }.forEach { address ->
        if (!addresses.contains(address)) addresses.add(address)
      }
    }
    return addresses
  }
}


