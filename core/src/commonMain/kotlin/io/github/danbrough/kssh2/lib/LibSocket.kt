package io.github.danbrough.kssh2.lib



expect object LibSocket {
  fun connect(hostName: String, port: Int = 22): SocketHandle

  fun close(socket: SocketHandle)
}