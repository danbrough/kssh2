package io.github.danbrough.kssh2

import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlin.time.Duration.Companion.seconds

private val log = demoLog

val scopeTest = basicCommand("scopeTest", "Misc scope tests") {

  ssh {
    testGetSshScope()
  }
}


class Thang : AutoCloseable {
  companion object {
    var COUNT = 1


  }

  init {
    log.warn { "CREATED THANG $COUNT" }
  }

  private val count = COUNT++

  override fun toString(): String = "Thang_$count"

  override fun close() {
    log.warn { "Thang::$count close()" }
  }
}

private val globalThang: Lazy<Thang> = lazy {
  Thang()
}

suspend fun thang(): Thang {
  if (!globalThang.isInitialized()) {
    log.debug { "thang() adding completion job .." }
    currentCoroutineContext().job.topJob.invokeOnCompletion {
      globalThang.value.close()
    }
  }
  return globalThang.value
}

@OptIn(ExperimentalCoroutinesApi::class)
private val Job.topJob: Job
  get() = parent?.topJob ?: this

suspend fun <R> thang(block: suspend Thang.() -> R): R = thang().block()


private suspend fun testGetSshScope() {
  log.info { "testGetSshScope()" }

  thang {
    log.debug { "in thang:${thang()} scope: $this" }
  }

  thang {
    log.debug { "in second thang scope: $this" }
    test1()
  }

}

private suspend fun test1() {
  log.info { "test1() with thang: ${thang()}" }


  thang {
    log.debug { "test1() $this  with thang: ${thang()} .. having a sleep" }
    delay(2.seconds)
    log.debug { "test1() $this with thang: ${thang()} finishing" }
  }

  log.info { "test1() finishing with thang: ${thang()}" }

}

