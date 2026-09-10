package demo.test

import demo.test.lib.LibSSH2
import demo.test.lib.Session
import org.danbrough.klog.logger

class Thang {
  companion object {

    val logDemo = logger("SSH2_DEMO")

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

      logDemo.info {  "calling initJNI.." }
      initJNI()
      logDemo.debug {  "calling .. LibSSH2.initLib()" }
      LibSSH2.initLib()
      logDemo.debug {  "Creating a session..." }
      val session = Session.createSession(false)
      logDemo.debug {"got session: $session .. closing it .." }
      Session.close(session)
      logDemo.debug {  "calling .. LibSSH2.closeLib()" }
      LibSSH2.closeLib()
      logDemo.info {  "done" }
    }
  }
}