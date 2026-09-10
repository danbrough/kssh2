package demo.test.lib

import kotlinx.cinterop.CPointer
import platform.android.JNIEnvVar
import platform.android.jclass

@CName("Java_demo_test_lib_LibSSH2_initLib")
fun ssh2InitLib(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("Initializing libssh2 ..")
}