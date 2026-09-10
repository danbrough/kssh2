package demo.test.lib

import demo.test.ssh2.cinterops.libssh2_init
import kotlinx.cinterop.CPointer
import platform.android.JNIEnvVar
import platform.android.jclass

@CName("Java_demo_test_lib_LibSSH2_initLib")
fun ssh2InitLib(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("Initializing libssh2 ..")
  libssh2_init(0).also {
    println("libssh2_init(0) returned $it")
  }
}