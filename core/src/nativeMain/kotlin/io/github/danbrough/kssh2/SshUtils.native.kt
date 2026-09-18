package io.github.danbrough.kssh2

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.danbrough.klog.logger
import platform.posix.AF_UNSPEC
import platform.posix.NI_MAXHOST
import platform.posix.NI_NUMERICHOST
import platform.posix.SOCK_STREAM
import platform.posix.addrinfo
import platform.posix.atexit
import platform.posix.freeaddrinfo
import platform.posix.gai_strerror
import platform.posix.getaddrinfo
import platform.posix.getnameinfo
import platform.posix.memset
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
private val exitBlocks = AtomicReference<List<() -> Unit>>(emptyList())

// Fix: Use AtomicBoolean to avoid boxing identity issues entirely
@OptIn(ExperimentalAtomicApi::class)
private val isRegistered = AtomicBoolean(false)

@OptIn(ExperimentalAtomicApi::class)
private val staticExitHandler = staticCFunction<Unit> {
  val blocks = exitBlocks.load()
  for (block in blocks) {
    try {
      block()
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }
}


internal val logNative = logger("SSH2_NATIVE")

actual object SshUtils {
  actual fun getEnv(name: String): String? = platform.posix.getenv(name)?.toKString()
  actual fun threadName(): String = "PThread[${platform.posix.pthread_self()}]"

  actual fun resolveHostName(hostName: String): List<String> = memScoped {
    val ipList = mutableListOf<String>()

    // 1. Set up the hints filter
    val hints = alloc<addrinfo>()
    memset(hints.ptr, 0, sizeOf<addrinfo>().convert())
    hints.ai_family = AF_UNSPEC     // Accept both IPv4 and IPv6
    hints.ai_socktype = SOCK_STREAM // Stream socket type (TCP)

    // 2. Call getaddrinfo
    val resultVar = allocPointerTo<addrinfo>()
    val status = getaddrinfo(hostName, null, hints.ptr, resultVar.ptr)

    if (status != 0) {
      val errorMsg = gai_strerror(status)?.toKString() ?: "Unknown error"
      println("Failed to resolve $hostName: $errorMsg")
      return emptyList()
    }

    // 3. Iterate through the linked list of addresses
    var currentResult: addrinfo? = resultVar.value?.pointed
    while (currentResult != null) {
      val bufferSize = NI_MAXHOST
      val ipBuffer = allocArray<ByteVar>(bufferSize)

      // getnameinfo extracts the IP address text string directly from the current result structure
      val flags = NI_NUMERICHOST // Force numeric IP output instead of a reverse-DNS lookup
      val statusGet = getnameinfo(
        currentResult.ai_addr, currentResult.ai_addrlen,
        ipBuffer, bufferSize.convert(),
        null, 0u, flags
      )

      if (statusGet == 0) {
        ipList.add(ipBuffer.toKString())
      }

      currentResult = currentResult.ai_next?.pointed
    }


    // 4. Free the memory allocated by getaddrinfo
    freeaddrinfo(resultVar.value)

    return ipList.distinct() // Deduplicate in case multiple records point to the same IP
  }

  @OptIn(ExperimentalAtomicApi::class)
  actual fun atExit(block: () -> Unit) {
    do {
      val current = exitBlocks.load()
      val next = current + block
    } while (!exitBlocks.compareAndSet(current, next))

    // Safe, clean, and no warning suppression needed
    if (isRegistered.compareAndSet(expectedValue = false, newValue = true)) {
      atexit(staticExitHandler)
    }
  }

}


