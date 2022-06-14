package tk.nikomitk.dooropenerhalfnew

import com.google.gson.Gson

data class Storage(
    var ipAddress: String? = null,
    var pin: Int? = null,
    var token: String? = null,
    var rememberPin: Boolean = false,
    var otps: ArrayList<Otp> = ArrayList(),
)
val gson = Gson()

fun Storage.toJson(): String = gson.toJson(this)
fun String.toStorage(): Storage = gson.fromJson(this, Storage::class.java)