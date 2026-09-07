@file:OptIn(InternalKotlinGradlePluginApi::class)

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  //alias(libs.plugins.androidApplication) apply false
  //alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.compose.hotreload) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kmp) apply false
  alias(libs.plugins.kmp.android.library) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.application) apply false
  //alias(libs.plugins.android.kotlin.multiplatform.library) apply false
  alias(libs.plugins.dokka)
  alias(libs.plugins.androidx.room) apply false
  //alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.shadow) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.koin) apply false
}

group = project.property("project.group").toString()
version = project.property("project.version").toString()


allprojects {
  group = project.rootProject.group
  version= project.rootProject.version


  afterEvaluate {
    extensions.findByType<KotlinMultiplatformExtension>()?.run {

      compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")

        optIn =
          listOf(
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlinx.cinterop.ExperimentalForeignApi",
            "kotlin.experimental.ExperimentalNativeApi"
          )
      }
    }



    tasks.withType<AbstractTestTask> {

      if (this is Test) {
        useJUnitPlatform()
      }

      testLogging {
        events = setOf(
          TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED
        )
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        showStackTraces = true
      }

      outputs.upToDateWhen {
        false
      }
    }
  }
}


