package tk.nikomitk.dooropenerhalfnew

import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtil {
    suspend fun sendMessage(
        type: String,
        token: String,
        content: String,
        ipAddress: String
    ): Response {
        val message = Message(type, token, content)
        val test: Deferred<Response> = coroutineScope {
            async (Dispatchers.IO) {
                val socket = Socket()
                try{
                    socket.connect(InetSocketAddress(ipAddress, 5687), 1500)
                    PrintWriter(socket.getOutputStream(), true).println(Gson().toJson(message))
                    return@async Gson().fromJson(
                        BufferedReader(InputStreamReader(socket.getInputStream())).readLine(),
                        Response::class.java
                    )
                } catch (exception: Exception) {
                    return@async Response("Timeout :c", "timeout")
                }
            }
        }
        return test.await()
    }
}