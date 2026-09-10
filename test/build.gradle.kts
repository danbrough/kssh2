import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
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
    macosX64()
  }

  targets.withType<KotlinNativeTarget>().configureEach {
    compilations["main"].cinterops.create("jni") {
      defFile(project.file("src/cinterop/jni.def"))
      compilerOpts("-I${project.file("src/headers")}")
      if (konanTarget.family.isAppleFamily)
        compilerOpts("-I${project.file("src/headers/darwin")}")
      else
        compilerOpts("-I${project.file("src/headers/linux")}")
    }
    binaries {

      sharedLib("thang", buildTypes = setOf(NativeBuildType.DEBUG)) {

      }
    }
  }
}

tasks.withType<ShadowJar> {
  mainClass = "demo.test.Thang"
  //duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
