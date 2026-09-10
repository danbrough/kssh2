@file:Suppress("SpellCheckingInspection")

package io.github.danbrough.kssh2.lib

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import org.danbrough.klog.logger
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jlong

val log = logger("SSH2_JNI")


const val JNI_PREFIX = "Java_io_github_danbrough_kssh2_lib_LibSSH2"


/*@CName("Java_io_github_danbrough_kssh2_lib_LibSSH2_testJNI")
fun ssh2TestJNI(env: CPointer<JNIEnvVar>, clz: jclass) {
  log.info { "testJNI worked!" }
}
*/
/**
 * The JNI-exported function.
 * Java expects the naming convention: Java_package_name_ClassName_methodName
 * Under the hood, JNI passes 'env' (JNIEnv*) and 'clazz' (jclass) as the first two parameters.
 */
@Suppress("FunctionName")
@CName("Java_io_github_danbrough_kssh2_lib_LibSSH2_testJNI")
fun testJNI(env: COpaquePointer?, clazz: COpaquePointer?) {
  println("Hello from Kotlin/Native via JNI on macOS!")
}

@CName("${JNI_PREFIX}_initLib")
fun ssh2Init(env: CPointer<JNIEnvVar>, clz: jclass) {
  log.info { "${JNI_PREFIX}_ssh2Init()" }
  LibSSH2.initLib()
}

@CName("${JNI_PREFIX}_closeLib")
fun ssh2Close(env: CPointer<JNIEnvVar>, clz: jclass) {
//  log.trace { "${JNI_PREFIX}_ssh2Close()" }
  log.info { "${JNI_PREFIX}_closeLib()" }
  LibSSH2.closeLib()
}


@CName("${JNI_PREFIX}_00024Agent_close")
fun ssh2AgentClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  agent: jlong
) = LibSSH2.Agent.close(agent)

/*
actual external fun waitSocket(session: SessionPtr, socket: SocketHandle): Long

 */
/*
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.toCPointer
import org.danbrough.ssh2.SSHNative
import org.danbrough.ssh2.log
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jlong
import platform.android.jobject

private const val JNI_PREFIX = "Java_org_danbrough_ssh2_SSHJni"


@CName("${JNI_PREFIX}_nativeCreate")
fun sshCreate(env: CPointer<JNIEnvVar>, clazz: jclass, obj: jobject): jlong {
  jniInit()
  log.trace { "__nativeInitSSH::nativeInit() " }
  return StableRef.create(SSHNative()).asCPointer().rawValue.toLong()
}


@CName("${JNI_PREFIX}_nativeDestroy")
fun sshDestroy(env: CPointer<JNIEnvVar>, clazz: jclass, ref: jlong) {
  jniInit()
  val sshRef = ref.toCPointer<COpaquePointerVar>()?.asStableRef<SSHNative>()
  log.trace { "__nativeDestroySSH:: destroying: $sshRef " }
  sshRef?.also {
    it.get().close()
    it.dispose()
  }
}

*/