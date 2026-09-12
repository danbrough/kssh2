package io.github.danbrough.kssh2.lib

import kotlinx.cinterop.CPointer
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jlong

@CName("${JNI_PREFIX_PACKAGE}_LibAgent_close")
fun ssh2AgentClose(
  env: CPointer<JNIEnvVar>,
  clazz: jclass,
  agent: jlong
) = LibSSH2.Agent.close(agent)