@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.util.Date

plugins {
  id("io.github.danbrough.kssh2.kmp")

  alias(libs.plugins.kmp.android.library)

}


kotlin {

  applyDefaultHierarchyTemplate()

  targets.all {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions {
          freeCompilerArgs.add("-Xklib-duplicated-unique-name-strategy=allow-all-with-warning")

        }
      }
    }
  }

  android {
    compileSdk { version = release(37) }
    minSdk = 27
    namespace = "io.github.danbrough.kssh2"

    packaging {
      jniLibs {
        useLegacyPackaging = true
      }
    }
  }

  androidNativeArm64()
  androidNativeX64()

  sourceSets {

    commonMain {
      dependencies {
        implementation(
          if (project.hasProperty("klog.path")) libs.klog
          else libs.klog.versioned
        )
        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinx.io)
        implementation(libs.kotlinx.coroutines.core)
      }
    }


    val jvmSharedMain = create("jvmSharedMain") {
      dependsOn(commonMain.get())
    }


    androidMain {
      dependsOn(jvmSharedMain)
    }

    jvmMain {
      dependsOn(jvmSharedMain)
    }

    val jniMain = create("jniMain") {
      dependsOn(nativeMain.get())
    }


    val inIde = System.getProperty("idea.active") != null

    if (inIde) {
      /**
       * Symlinked to jniMain so that the JNI cinterop files are available in the IDE.
       */
      val androidJniMain = create("androidJniMain") {
        dependsOn(nativeMain.get())
      }


      androidNativeMain {
        dependsOn(androidJniMain)
      }
    } else {
      androidNativeMain {
        dependsOn(jniMain)
      }
    }


    linuxMain {
      dependsOn(jniMain)
    }

    macosMain {
      dependsOn(jniMain)
    }
  }

  val ssh2DefFileTemplate = project.file("src/cinterop/ssh2_template.def")
  val ssh2DefFile = project.file("src/cinterop/ssh2.def")
  val libPath = project.file("../lib/").absolutePath

  val generateDefFileTaskName = "generateDefFile"
  tasks.register(generateDefFileTaskName) {

    description = "Generates $ssh2DefFile"


    inputs.file(ssh2DefFileTemplate)
    outputs.file(ssh2DefFile)
    doFirst {
      println("$name::generating $ssh2DefFile")
    }
    /*
compilerOpts.linux_x64 = -I/files/workspace/kssh2/lib/openssl/linux/x64/include/ -I/files/workspace/kssh2/lib/ssh2/linux/x64/include/
compilerOpts.linux_arm64 = -I/files/workspace/kssh2/lib/openssl/linux/arm64/include/ -I/files/workspace/kssh2/lib/ssh2/linux/arm64/include/

compilerOpts.android_x64 = -fPIC  -lkssh2 -I/files/workspace/kssh2/lib/ssh2/android/x86_64/include
compilerOpts.android_arm64 = -fPIC  -lkssh2  -mno-outline-atomics  -I/files/workspace/kssh2/lib/ssh2/android/arm64-v8a/include



linkerOpts.android_x64 = -lz  -ldl  /files/workspace/kssh2/lib/ssh2/android/x86_64/lib/libssh2.a \
/files/workspace/kssh2/lib/openssl/android/x86_64/lib/libcrypto.a /files/workspace/kssh2/lib/openssl/android/x86_64/lib/libssl.a
linkerOpts.android_arm64 = -lz  -ldl  /files/workspace/kssh2/lib/ssh2/android/arm64-v8a/lib/libssh2.a \
/files/workspace/kssh2/lib/openssl/android/arm64-v8a/lib/libcrypto.a /files/workspace/kssh2/lib/openssl/android/arm64-v8a/lib/libssl.a

linkerOpts.linux_x64 = -lz -lpthread -ldl /files/workspace/kssh2/lib/openssl/linux/x64/lib/libssl.a /files/workspace/kssh2/lib/openssl/linux/x64/lib/libcrypto.a /files/workspace/kssh2/lib/ssh2/linux/x64/lib/libssh2.a
linkerOpts.linux_arm64 = -lz -lpthread -ldl /files/workspace/kssh2/lib/openssl/linux/arm64/lib/libssl.a /files/workspace/kssh2/lib/openssl/linux/arm64/lib/libcrypto.a /files/workspace/kssh2/lib/ssh2/linux/arm64/lib/libssh2.a


     */
    actions.add {
      val footer = ssh2DefFileTemplate.readText()
      ssh2DefFile.printWriter().use { output ->
        output.println("# GENERATED. Edit ${ssh2DefFileTemplate.name} instead.")

        listOf("arm64", "x64").forEach { arch ->
          output.println("compilerOpts.linux_$arch = -I$libPath/openssl/linux/$arch/include -I$libPath/ssh2/linux/$arch/include")
          output.println("linkerOpts.linux_$arch = $libPath/openssl/linux/$arch/lib/libssl.a $libPath/openssl/linux/$arch/lib/libcrypto.a $libPath/ssh2/linux/$arch/lib/libssh2.a")
          val libDir = if (arch == "arm64") "arm64-v8a" else "x86_64"
          output.println("compilerOpts.android_$arch = -fPIC  -mno-outline-atomics -I$libPath/openssl/android/$libDir/include -I$libPath/ssh2/android/$libDir/include")
          output.println("linkerOpts.android_$arch = -Wl,--undefined=EVP_aes_256_gcm  $libPath/openssl/android/$libDir/lib/libssl.a $libPath/openssl/android/$libDir/lib/libcrypto.a $libPath/ssh2/android/$libDir/lib/libssh2.a")
        }

        output.print(footer)
      }
    }
  }

  targets.withType<KotlinNativeTarget>().configureEach {

    compilations["main"].apply {

      cinterops.create("ssh2Interop") {
        defFile(project.file("src/cinterop/ssh2.def"))
        packageName("${project.group}.libssh2.cinterop")
        compilerOpts("-fPIC", "-I${project.file("src/cinterop")}")
        tasks[interopProcessingTaskName].dependsOn(generateDefFileTaskName)
/*
        val libDirPath = project.file("../lib").absolutePath

        if (konanTarget.family == Family.LINUX) {
          compilerOpts(listOf("openssl", "ssh2").map {
            "-I$libDirPath/$it/linux/${if (konanTarget == KonanTarget.LINUX_ARM64) "arm64" else "x64"}/include"
          })
          linkerOpts(
            listOf(
              "libssl.a",
              "libcrypto.a"
            ).map { "$libDirPath/openssl/linux/${if (konanTarget == KonanTarget.LINUX_ARM64) "arm64" else "x64"}/lib/$it" })
        } else if (konanTarget.family == Family.ANDROID) {
          compilerOpts(listOf("openssl", "ssh2").map {
            "-I$libDirPath/$it/android/${if (konanTarget == KonanTarget.ANDROID_ARM64) "arm64-v8a" else "x86_64"}/include"
          })
          linkerOpts(
            listOf(
              "libssl.a",
              "libcrypto.a"
            ).map { "$libDirPath/openssl/linux/${if (konanTarget == KonanTarget.ANDROID_ARM64) "arm64-v8a" else "x86_64"}/lib/$it" })
          linkerOpts("$libDirPath/ssh2/linux/${if (konanTarget == KonanTarget.ANDROID_ARM64) "arm64-v8a" else "x86_64"}/lib/libssh2.a")
        }
*/


      }

      if (konanTarget.family != Family.ANDROID)
        cinterops.create("jni") {
          /**
           * Create the JNI interops in package platform.android so that we can use platform.android for all targets
           */
          packageName("platform.android")
          header("./src/headers/jni.h")
          if (konanTarget.family == Family.LINUX)
            includeDirs("./src/cinterops", "./src/headers", "./src/headers/linux")
          else
            includeDirs("./src/cinterops", "./src/headers", "./src/headers/darwin")
        }
    }

    binaries {
      sharedLib("kssh2") {
      }
    }
  }
}

val copyJniLibsTask = tasks.register("copyJniLibs") {
  description = "Copies the shared libraries into the jni folders"
}.get()

kotlin.targets.withType<KotlinNativeTarget>().filter { it.konanTarget.family == Family.ANDROID }
  .map {
    Pair(it.binaries.findSharedLib("kssh2", NativeBuildType.DEBUG)?.linkTaskProvider!!.get(), it)
  }.forEach { (linkTask, target) ->
    println("LINK TASK: $linkTask name:${linkTask.name}")

    //val androidLibDir = project.file("app/libs/${linkTask.target}")
    val srcDir = linkTask.outputs.files.files.first()

    val outputDir = project.file("../app/libs/${target.konanTarget.abiFolder}")
    val copyLibTaskName = "copyLib${linkTask.name.substringAfter("link").capitalize()}"

    tasks.register(copyLibTaskName) {
      dependsOn(linkTask)
      description =
        "Copys the shared library from ${linkTask.name} to app/libs/${target.konanTarget.abiFolder}"
      actions.add {

        println("copying ${srcDir.absolutePath} to ${outputDir.absolutePath}")
        srcDir.copyRecursively(outputDir, overwrite = true)
      }
    }

    linkTask.finalizedBy(copyLibTaskName)
    copyJniLibsTask.dependsOn(copyLibTaskName)
  }


val KonanTarget.abiFolder: String
  get() = when (this) {
    KonanTarget.ANDROID_ARM64 -> "arm64-v8a"
    KonanTarget.ANDROID_X64 -> "x86_64"
    else -> error("Unsupported target: $this")
  }

