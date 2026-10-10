@file:Suppress("UnstableApiUsage")


rootProject.name = "kssh2"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    //maven("file:///files/cache/xtras/maven/")
    maven("https://maven.danbrough.org")
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
        includeGroupAndSubgroups("org.jetbrains")
      }
    }

    mavenCentral()
    gradlePluginPortal()

  }

  includeBuild("build-logic")


}

dependencyResolutionManagement {
  repositories {
    maven("https://maven.danbrough.org")
    //maven("file:///files/cache/xtras/maven/")
    google {
      mavenContent {
        includeGroupAndSubgroups("org.jetbrains")
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.60.6"
}



include(":core", ":demo",":androidApp",":composeApp")


val kattyPath = providers.gradleProperty("katty.path").orNull

if (!kattyPath.isNullOrEmpty()) {
  // 1. Tell Gradle to pull the entire local project directory into the build tree
  includeBuild(kattyPath) {
    // 2. Instruct Gradle to globally substitute the published artifact id
    // with the specific subproject path inside your local katty clone
    dependencySubstitution {
      substitute(module("io.github.danbrough.katty:shell"))
        .using(project(":shell")) // Assumes the submodule in katty is named ':shell'
    }
    dependencySubstitution {
      substitute(module("io.github.danbrough.katty:core"))
        .using(project(":core")) // Assumes the submodule in katty is named ':shell'
    }
  }
}

val klogPath = providers.gradleProperty("klog.path").orNull

if (!klogPath.isNullOrEmpty()) {
  // 1. Tell Gradle to pull the entire local project directory into the build tree
  includeBuild(klogPath) {
    // 2. Instruct Gradle to globally substitute the published artifact id
    // with the specific subproject path inside your local katty clone
    dependencySubstitution {
      substitute(module("org.danbrough.klog:core"))
        .using(project(":core")) // Assumes the submodule in katty is named ':shell'
    }
  }
}