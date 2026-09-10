package io.github.danbrough.kssh2.lib

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.jstring

class JniEnv(val env: CPointer<JNIEnvVar>) {
  val envPtr = env.pointed.pointed!!

  fun <R> jString(s: jstring, block: (String?) -> R): R {
    val ptr = envPtr.GetStringUTFChars!!(env, s, null)
    val r = block(ptr?.toKString())
    envPtr.ReleaseStringUTFChars!!(env, s, ptr)
    return r
  }

  fun <R> jString(s: jstring, t: jstring, block: (String?, String?) -> R): R =
    jString(s) { s ->
      jString(t) { t ->
        block(s, t)
      }
    }


  fun <R> jString(s: jstring, t: jstring, u: jstring, block: (String?, String?, String?) -> R): R =
    jString(s) { s ->
      jString(t, u) { t, u ->
        block(s, t, u)
      }
    }


  fun <R> jString(
    s: jstring,
    t: jstring,
    u: jstring,
    v: jstring,
    block: (String?, String?, String?, String?) -> R
  ): R =
    jString(s) { s ->
      jString(t, u, v) { t, u, v ->
        block(s, t, u, v)
      }
    }
}

fun <R> CPointer<JNIEnvVar>.jniEnv(block: JniEnv.() -> R): R =
  JniEnv(this).block()