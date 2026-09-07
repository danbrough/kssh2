import org.gradle.api.Project

fun Project.testStuff() {
  println("testStuff: XTRAS dir is ${project.findProperty("xtras.dir")}")

}