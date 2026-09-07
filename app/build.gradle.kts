plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "org.danbrough.ssh2"
  compileSdk {
    version = release(37)
  }
  packaging {
    jniLibs {
      useLegacyPackaging = true
    }
  }
/*
  targets.all {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions{
          freeCompilerArgs.add("-Xklib-duplicated-unique-name-strategy=allow-all-with-warning")
        }
      }
    }
  }*/

  defaultConfig {
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  sourceSets {
    getByName("main").jniLibs.directories.add("./libs")
  }

  buildTypes {
    release {
      optimization {
        enable = false
      }
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(projects.core)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  testImplementation(libs.junit)
  //androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.testExt.junit)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  //
  implementation(
    if (project.hasProperty("klog.path")) libs.klog
    else libs.klog.versioned
  )
}
afterEvaluate {

  tasks.getByName("mergeDebugJniLibFolders") {
    println("MERGE TASK TASK: $this type: ${this::class.java}")
    dependsOn(":core:copyJniLibs")
  }
}

