package demo.test.lib

import demo.test.ssh2.cinterops.libssh2_exit
import demo.test.ssh2.cinterops.libssh2_init
import kotlinx.cinterop.CPointer
import platform.android.JNIEnvVar
import platform.android.jclass

const val JNI_PREFIX = "Java_demo_test_lib"

@CName("${JNI_PREFIX}_LibSSH2_initLib")
fun ssh2InitLib(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("Initializing libssh2 ..")
  libssh2_init(0).also {
    println("libssh2_init(0) returned $it")
  }
}


@CName("${JNI_PREFIX}_LibSSH2_closeLib")
fun ssh2CloseLib(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("Closing libssh2 ..")
  libssh2_exit().also {
    println("libssh2_exit(0) returned")
  }
}