package io.github.danbrough.kssh2.lib

data class Result(val code: Int, override val message: String) : Exception("code: $message") {

  constructor(err: Exception): this(-1,err.message ?: "Unknown error")

  companion object {
    val SUCCESS = Result(0, "Success")
  }

  fun onSuccess(block: Result.() -> Unit): Result {
    if (this == SUCCESS)
      block()
    return this
  }

  fun onError(block: Result.() -> Unit): Result {
    if (this != SUCCESS)
      block()
    return this
  }

  fun throwIfError() {
    if (this != SUCCESS) throw this
  }
}

val Result.success: Boolean
  get() = this == Result.SUCCESS


val Result.takeIfSuccess: Result?
  get() = takeIf { it == Result.SUCCESS }

val Result.takeIfError:Result?
  get() = takeIf { it != Result.SUCCESS }

val Exception.asResult: Result
  get() = Result(this)