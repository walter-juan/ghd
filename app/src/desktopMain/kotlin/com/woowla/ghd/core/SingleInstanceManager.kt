package com.woowla.ghd.core

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.BindException
import java.io.IOException
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class SingleInstanceManager private constructor() {
    companion object {
        private const val PORT = 47263

        fun isPrimary(onRestoreRequest: () -> Unit): Boolean {
            return try {
                val server = ServerSocket(PORT, 1, InetAddress.getLoopbackAddress())
                thread(name = "ghd-single-instance", isDaemon = true) {
                    while (!server.isClosed) {
                        try {
                            server.accept().use {
                                SwingUtilities.invokeLater(onRestoreRequest)
                            }
                        } catch (_: IOException) {
                            server.close()
                        }
                    }
                }
                true
            } catch (_: BindException) {
                try {
                    Socket(InetAddress.getLoopbackAddress(), PORT).close()
                    false
                } catch (_: IOException) {
                    true
                }
            }
        }
    }
}
