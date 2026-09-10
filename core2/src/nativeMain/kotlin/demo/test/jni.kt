package demo.test

import kotlinx.cinterop.CPointer
import platform.android.JNIEnvVar
import platform.android.jclass

@CName("Java_demo_test_Thang_initJNI")
fun initJNIImpl(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("JNI WORKS!!")
}


