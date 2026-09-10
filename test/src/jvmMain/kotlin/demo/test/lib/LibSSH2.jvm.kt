package demo.test.lib

actual class LibSSH2 {
  actual companion object {
    @JvmStatic
    actual external fun initLib()

    @JvmStatic
    actual external fun closeLib()
  }
}