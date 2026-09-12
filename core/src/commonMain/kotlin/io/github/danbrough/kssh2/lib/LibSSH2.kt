package io.github.danbrough.kssh2.lib


typealias SocketHandle = Long
typealias SessionPtr = Long
typealias AgentPtr = Long
typealias ChannelPtr = Long


const val LIBSSH2_CHANNEL_WINDOW_DEFAULT = 2 * 1024 * 1024
const val LIBSSH2_CHANNEL_PACKET_DEFAULT = 32768


expect object LibSSH2 {

  fun initJNI()

  fun initLib()
  fun closeLib()

  val Session: LibSession
  val Socket: LibSocket
  val Channel: LibChannel
  val Agent: LibAgent


}