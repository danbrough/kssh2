package io.github.danbrough.kssh2.lib

import io.github.danbrough.libssh2.cinterop.ssh2_socket_close
import io.github.danbrough.libssh2.cinterop.ssh2_socket_connect
import kotlinx.cinterop.convert

actual object LibSocket {
  actual fun connect(hostName: String, port: Int): SocketHandle =
    ssh2_socket_connect(hostName, port).convert()

  actual fun close(socket: SocketHandle) {
    if (socket != 0L)
      ssh2_socket_close(socket.toInt())
  }
}