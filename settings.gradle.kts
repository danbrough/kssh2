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


include(":core", ":demo",":app")


providers.gradleProperty("klog.path").orNull?.also {
  includeBuild(it)
}

providers.gradleProperty("katty.path").orNull?.also {
  includeBuild(it)
}


