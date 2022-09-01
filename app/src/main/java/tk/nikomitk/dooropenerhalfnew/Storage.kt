package tk.nikomitk.dooropenerhalfnew

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import java.io.File

data class Storage(
    var ipAddress: String? = null,
    var pin: Int? = null,
    var token: String? = null,
    var rememberPin: Boolean = false,
    var otps: ArrayList<Otp> = ArrayList(),
    var widgetTime: Int = 2
)
val gson = Gson()

fun Storage.toJson(): String = gson.toJson(this)
fun String.toStorage(): Storage = gson.fromJson(this, Storage::class.java)
fun Storage.save(file: File) = file.writeText(toJson())

fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()