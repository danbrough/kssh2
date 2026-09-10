package demo.test

import demo.test.lib.LibSSH2
import demo.test.lib.Session

class Thang {
  companion object {

    init {
      println("loading thang library ..")
      System.loadLibrary("thang")
      println("thang lib loaded")
    }

    @JvmStatic
    external fun initJNI()

    @JvmStatic
    fun main(args: Array<String>) {
      commonMain(args.toList())

      println("calling initJNI..")
      initJNI()
      println("calling .. LibSSH2.initLib()")
      LibSSH2.initLib()
      println("Creating a session...")
      val session = Session.createSession(false)
      println("got session: $session .. closing it ..")
      Session.close(session)
      println("calling .. LibSSH2.closeLib()")
      LibSSH2.closeLib()
    }
  }
}