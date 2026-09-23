import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kmp)

  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose.hotreload)
  alias(libs.plugins.shadow)
  alias(libs.plugins.kmp.android.library)
}

kotlin {
  applyDefaultHierarchyTemplate()
  android {
    namespace = "io.github.danbrough.kssh2"

    compileSdk {
      version = release(37)
    }

    androidResources {
      enable = true
    }

    withHostTest {

    }

    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }

    /*    packaging {
          resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts.add("META-INF/io.netty.versions.properties")
            pickFirsts.add("META-INF/INDEX.LIST")
            pickFirsts.add("META-INF/NOTICE.md")
            pickFirsts.add("META-INF/LICENSE.md")
          }
        }*/
  }


  jvm {

  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.core)
      }
    }
  }
}
