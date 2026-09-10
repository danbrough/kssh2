import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  id("io.github.danbrough.kssh2.kmp")
  alias(libs.plugins.shadow)
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {

  targets.all {
    compilations.all {

      compilerOptions.configure {
        // Allows the build to proceed by warning instead of crashing
        //freeCompilerArgs.add("-Xklib-duplicated-unique-name-strategy=allow-all-with-warning")
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core)
        implementation(project.findProperty("katty.path")?.let { libs.katty }
          ?: libs.katty.versioned)
        implementation(project.findProperty("klog.path")?.let { libs.klog }
          ?: libs.klog.versioned)

      }
    }

    jvmMain {
      dependencies {

        implementation(libs.kotlinx.datetime)
        implementation(libs.sshtools.client)
        implementation(libs.sshtools.bc)
      }
    }
  }

  targets.withType<KotlinNativeTarget>().configureEach {
    binaries {
      executable("ssh2demo") {
        entryPoint = "io.github.danbrough.kssh2.main"
      }
    }
  }
}


tasks.withType<ShadowJar> {
  mainClass = "io.github.danbrough.kssh2.MainKt"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

afterEvaluate {
  val arch = HostManager.hostArch()
  println("HOST ARCH: $arch")


  tasks.register("getShadowJar") {
    description = "Creates and prints the name of the shadow jar"
    //dependsOn(sharedLibTask)
    dependsOn("shadowJar")

    val shadowFile = tasks["shadowJar"].outputs.files
    val nodeExecutable = tasks.withType<NodeJsExec>().firstOrNull()?.executable

    doFirst {
      println("shadowJar: ${shadowFile.files.first()}")
      println("nodeExecutable: $nodeExecutable")
    }
  }
}

