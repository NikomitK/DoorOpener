package tk.nikomitk.dooropenerhalfnew

import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import tk.nikomitk.dooropenerhalfnew.messagetypes.LoginMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.Response
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

object NetworkUtil {
    // TODO unite the methods with message: String parameter
    private val gson: Gson = Gson()

    suspend fun sendMessage(
        message: String,
        ipAddress: String
    ): Response {
        val test: Deferred<Response> = coroutineScope {
            async(Dispatchers.IO) {
                val response = sendTLsMessage(message, ipAddress)
                return@async response ?: run {
                    val socket = Socket()
                    try {
                        socket.connect(InetSocketAddress(ipAddress, 5687), 1500)
                        PrintWriter(socket.outputStream, true).println(message)
                        return@async Gson().fromJson(
                            BufferedReader(InputStreamReader(socket.inputStream)).readLine(),
                            Response::class.java
                        )
                    } catch (exception: Exception) {
                        return@async Response("Timeout :c", "timeout")
                    } catch (exception: java.lang.Exception) {
                        exception.printStackTrace()
                        return@async Response(exception.message!!, "Not sent")
                    }
                }
            }
        }
        return test.await().takeUnless {
            it.text.isBlank()
        } ?: Response("Invalid response", "invalid response")
    }

    suspend fun sendLoginMessage(
        ipAddress: String,
        pin: Int,
        storage: Storage,
        newDevice: Boolean,
        rememberPassword: Boolean,
        storageFile: File,
        tls: Boolean
    ): Response {
        val loginMessage = LoginMessage("login", pin, newDevice)
        val test: Deferred<Response> = coroutineScope {
            async(Dispatchers.IO) {
                val response = sendTLsMessage(gson.toJson(loginMessage), ipAddress)
                return@async response ?: run {
                    val socket = Socket()
                    try {
                        socket.connect(InetSocketAddress(ipAddress, 5687), 1500)
                        PrintWriter(socket.outputStream, true).println(gson.toJson(loginMessage))
                        return@async Gson().fromJson(
                            BufferedReader(InputStreamReader(socket.inputStream)).readLine(),
                            Response::class.java
                        )
                    } catch (exception: Exception) {
                        return@async Response("Timeout :c", "timeout")
                    } catch (exception: java.lang.Exception) {
                        exception.printStackTrace()
                        return@async Response(exception.message!!, "Not sent")
                    }
                }
            }
        }
        return test.await()
    }

    private fun sendTLsMessage(message: String, ipAddress: String): Response? {
        return try {
            val socket = SSLSocketFactory.getDefault().createSocket() as SSLSocket
            println("vor connect")
            socket.connect(InetSocketAddress(ipAddress, 5688), 1000)
            socket.soTimeout = 1000
            println("nach connect")
            PrintWriter(socket.outputStream, true).println(message)
            Gson().fromJson(
                BufferedReader(InputStreamReader(socket.inputStream)).readLine(),
                Response::class.java
            )

        } catch (exception: Exception) {
            println("catch")
            null
        }
    }

}