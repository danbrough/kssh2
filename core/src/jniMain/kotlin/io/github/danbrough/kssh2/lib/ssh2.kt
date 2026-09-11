@file:Suppress("SpellCheckingInspection")

package io.github.danbrough.kssh2.lib

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.value
import org.danbrough.klog.logger
import platform.android.JNIEnvVar
import platform.android.JNINativeMethod
import platform.android.JNI_ERR
import platform.android.JNI_OK
import platform.android.JNI_VERSION_1_6
import platform.android.JavaVMVar
import platform.android.jclass
import platform.android.jint
import platform.android.jlong



import platform.posix.* // For pointer tracking if needed

val log = logger("SSH2_JNI")

/*@CName("JNI_OnLoad")
fun jniOnLoad(vm: CPointer<JavaVMVar>, reserved: CPointer<out Any>?): jint {
  //initRuntimeIfNeeded()

  memScoped {
    val envPtr = alloc<CPointerVar<JNIEnvVar>>()
    val vmMethods = vm.pointed.pointed!!

    // Retrieve the JNIEnv pointer from the JVM instance
    if (vmMethods.GetEnv!!(vm, envPtr.ptr.reinterpret(), JNI_VERSION_1_6) != JNI_OK) {
      return JNI_ERR
    }

    val env = envPtr.value!!
    val envMethods = env.pointed.pointed!!

    // 1. Locate your Java class
    val className = "io/github/danbrough/kssh2/lib/LibSSH2"
    val clazz = envMethods.FindClass!!(env, className.cstr.ptr) ?: return JNI_ERR

    // 2. Register your native methods manually
    val methods = allocArray<JNINativeMethod>(1)
    methods[0].name = "initJNI".cstr.ptr
    methods[0].signature = "()V".cstr.ptr // Change signature to match your actual Java method!
    methods[0].fnPtr = staticCFunction(::initJNIImpl).reinterpret()

    if (envMethods.RegisterNatives!!(env, clazz, methods, 1) < 0) {
      return JNI_ERR
    }
  }

  return JNI_VERSION_1_6
}*/

const val JNI_PREFIX_PACKAGE = "Java_io_github_danbrough_kssh2_lib"
private const val JNI_PREFIX = "${JNI_PREFIX_PACKAGE}_LibSSH2"

@CName("initJniBridge")
fun initJniBridge() {
  // Leave empty. This tells the compiler the file is a global runtime root.
}

// A normal Kotlin native function matching the JNI signature
fun initJNIImpl(env: CPointer<JNIEnvVar>, clz: jclass) {
  log.info { "Hello from Kotlin/Native via Dynamic JNI!" }
}
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
@CName("Java_io_github_danbrough_kssh2_lib_LibSSH2_initJNI")
fun initJNI(env: CPointer<JNIEnvVar>, clz: jclass) {
  println("Hello from Kotlin/Native via JNI")
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