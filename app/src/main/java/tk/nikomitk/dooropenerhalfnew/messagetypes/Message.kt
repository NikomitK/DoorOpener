package tk.nikomitk.dooropenerhalfnew.messagetypes

import com.google.gson.Gson


data class Message(val type: String, val token: String, val content: String)

val gson = Gson()
fun Message.toJson(): String = gson.toJson(this)
fun LoginMessage.toJson(): String = gson.toJson(this)
