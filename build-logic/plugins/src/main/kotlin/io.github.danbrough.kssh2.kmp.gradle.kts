import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  kotlin("multiplatform")
}

kotlin {

  jvm {
    compilerOptions {
      //jvmTarget = JvmTarget.JVM_17
    }
  }


  linuxX64()
  //if (HostManager.hostArch() == "aarch64")
    linuxArm64()

  if (HostManager.hostIsMac) {
    macosX64()
    macosArm64()
  }

  compilerOptions {
    languageVersion = KotlinVersion.KOTLIN_2_2
  }


}

