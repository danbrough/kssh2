package io.github.danbrough.kssh2

import io.github.danbrough.katty.CommandExecutor
import io.github.danbrough.katty.basicCommand
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlin.time.Duration.Companion.seconds

private val log = demoLog

val scopeTest = basicCommand("scopeTest", "Misc scope tests") {

  ssh {
    log.debug { "scopeTest:: scope: $this" }
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


private val Job.topJob: Job
  get() = parent?.topJob ?: this

private suspend fun topJob(): Job = currentCoroutineContext().job.topJob

suspend fun <R> thang(block: suspend Thang.() -> R): R = thang().block()


private suspend fun testGetSshScope() {
  log.info { "testGetSshScope()" }

  ssh {
    log.debug { "testGetSshScope::ssh scope: $this" }


    log.debug { "topJob1: ${currentCoroutineContext().job.topJob}" }
    log.debug { "topJob1: ${topJob()}" }
    log.debug { "supervisor: ${currentCoroutineContext()[CommandExecutor]?.supervisorJob}" }

    thang {
      log.debug { "in thang:${thang()} scope: $this" }
    }

    thang {
      log.debug { "in second thang scope: $this" }
      test1()
    }
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

