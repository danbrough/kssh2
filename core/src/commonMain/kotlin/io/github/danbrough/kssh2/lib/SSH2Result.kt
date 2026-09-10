package io.github.danbrough.kssh2.lib

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class SSH2Result(val code: Int, val message: String = "Unknown error") {
  companion object {
    val SUCCESS = SSH2Result(0, "Success")
  }

  val isSuccess: Boolean
    get() = code == 0

  override fun equals(other: Any?): Boolean = (other is SSH2Result) && other.code == this.code
  override fun hashCode(): Int = code
  override fun toString(): String = "Result[$code:$message]"
}


fun SSH2Result.successOrThrow() {
  if (this != SSH2Result.SUCCESS)
    error(message)
}


@OptIn(ExperimentalContracts::class)
inline fun SSH2Result.onSuccess(block: () -> Unit): SSH2Result {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }
  if (isSuccess) block()
  return this
}


@OptIn(ExperimentalContracts::class)
inline fun SSH2Result.onFailure(block: SSH2Result.() -> Unit): SSH2Result {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }
  if (!isSuccess) block(this)
  return this
}


