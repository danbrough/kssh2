import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  alias(libs.plugins.kmp)
  alias(libs.plugins.shadow)
}


kotlin {
  jvm()
  if (HostManager.hostIsLinux) {
    linuxX64()
  } else {
    @Suppress("DEPRECATION")
    macosX64()
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(
          if (project.hasProperty("klog.path")) libs.klog
          else libs.klog.versioned
        )
      }
    }
  }
  targets.withType<KotlinNativeTarget>().configureEach {
    compilations["main"].apply {
      cinterops.create("jni") {
        //defFile(project.file("src/cinterop/jni.def"))
        packageName("platform.android")
        header(project.file("src/headers/jni.h"))

        compilerOpts("-I${project.file("src/headers")}")
        if (konanTarget.family.isAppleFamily) {
          header(project.file("src/headers/darwin/jni_md.h"))
          compilerOpts("-I${project.file("src/headers/darwin")}")
        } else {
          header(project.file("src/headers/linux/jni_md.h"))
          compilerOpts("-I${project.file("src/headers/linux")}")
        }
      }

      cinterops.create("ssh2") {
        packageName("demo.test.ssh2.cinterops")
//        headers = libssh2.h  libssh2_publickey.h  libssh2_sftp.h
        val headerNames = listOf("libssh2.h", "libssh2_publickey.h", "libssh2_sftp.h")
        if (konanTarget.family.isAppleFamily) {
          headers(headerNames.map { file("/usr/local/include/$it") })
        } else if (konanTarget.family == Family.LINUX) {
          headers(headerNames.map { file("/usr/include/$it") })
        }
        compilerOpts("-I/usr/include", "-I/usr/local/include")
      }
    }
    binaries {
      sharedLib("thang", buildTypes = setOf(NativeBuildType.DEBUG)) {
        linkerOpts("-L/usr/lib/", "-L/usr/local/lib/", "-lssh2")
      }
    }
  }
}

tasks.withType<ShadowJar> {
  mainClass = "demo.test.Thang"
  //duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
