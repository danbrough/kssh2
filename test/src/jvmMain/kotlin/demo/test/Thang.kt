package demo.test

class Thang {
  companion object {
    @JvmStatic
    external fun initJNI()

    @JvmStatic
    fun main(args: Array<String>) {
      commonMain(args.toList())

      println("loading thang library ..")
      System.loadLibrary("thang")
      println("calling initJNI..")
      initJNI()
    }
  }
}