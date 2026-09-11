package io.github.danbrough.kssh2.lib

actual object LibSocket {
  @JvmStatic
  actual external fun connect(
    hostName: String,
    port: Int
  ): SocketHandle

  @JvmStatic
  actual external fun close(socket: SocketHandle)
}