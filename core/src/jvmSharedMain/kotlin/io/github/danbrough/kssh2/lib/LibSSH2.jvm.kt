package io.github.danbrough.kssh2.lib

import io.github.danbrough.kssh2.logJvm
import org.danbrough.klog.logger

private val log = logJvm

@Suppress("UnsafeDynamicallyLoadedCode")
actual object LibSSH2 {



  actual val Session: LibSession = LibSession
  actual val Socket: LibSocket = LibSocket
  actual val Channel: LibChannel = LibChannel
  actual val Agent: LibAgent = LibAgent

  init {
    try {
      //log.debug { "JNISupport: loading ssh2 library.." }
      //System.loadLibrary("ssh2")
      log.debug { "JNISupport: loading kssh2 library.." }
      System.loadLibrary("kssh2")
      log.debug { "JNISupport: kssh2 library loaded" }

    } catch (e: UnsatisfiedLinkError) {
      log.error(e) { "Failed to load library" }
      throw e
    }
  }

  @JvmStatic
  actual external fun initJNI()

  @JvmStatic
  actual external fun initLib()

  @JvmStatic
  actual external fun closeLib()

  @JvmStatic
  external fun test1()





}