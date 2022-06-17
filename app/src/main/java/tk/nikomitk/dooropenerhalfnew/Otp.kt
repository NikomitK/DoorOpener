package tk.nikomitk.dooropenerhalfnew

data class Otp(val pin: String, val expirationDate: String)
fun Otp.toJson(): String = gson.toJson(this)
