package io.github.danbrough.kssh2

import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

private val log = demoLog

val scopeTest = basicCommand("scopeTest", "Misc scope tests") {

  ssh {
    testGetSshScope()
  }
}


class Thang : CoroutineContext.Element, AutoCloseable {
  companion object : CoroutineContext.Key<Thang> {
    var COUNT = 1


  }

  init {
    log.warn { "CREATED THANG $COUNT" }
  }

  private val count = COUNT++

  override fun toString(): String = "Thang_$count"

  override val key: CoroutineContext.Key<*> = Thang
  override fun close() {
    log.info { "Thang::$count close()" }
  }
}
suspend fun <R> thang(block: suspend Thang.() -> R) =
  currentCoroutineContext()[Thang]?.block() ?: Thang().also { thang ->
    withContext(thang) {
      thang.use {
        it.block()
      }
    }
  }


private suspend fun testGetSshScope() {
  log.info { "testGetSshScope()" }

  thang {
    log.debug { "in thang scope: $this" }
  }

  thang {
    log.debug { "in second thang scope: $this" }
    test1()
  }

}

private suspend fun test1() {
  log.debug { "test1() with thang: ${currentCoroutineContext()[Thang]}" }

  coroutineScope {
    launch(Dispatchers.IO) {
      thang {
        log.debug { "test1() $this  with thang: ${currentCoroutineContext()[Thang]} .. having a sleep" }
        delay(2.seconds)
        log.debug { "test1() $this finishing" }
      }
    }
  }
}