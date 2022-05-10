package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.messagetypes.LoginMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.Response
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// please don't question my use of expression marks at the end of every displayed string :)

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO remember pin check etc, load ip address from storage, hash pin
        val storageFile: File = File(applicationContext.filesDir, "storageFile")

        if (intent.getBooleanExtra("logout", false)) {
            storageFile.writeText("")
        }

        var storage: Storage = Storage()

        if (!storageFile.createNewFile() && storageFile.readText().contains(":")) {
            storage = Gson().fromJson(storageFile.readText(), Storage::class.java)
            val intent = Intent(this, OpenActivity::class.java).apply {
                putExtra("ipAddress", storage.ipAddress)
                putExtra("token", storage.token)
            }
            startActivity(intent)
            finish()
        }


        val textAddress: EditText = findViewById(R.id.editTextAddress)
        val textPin: EditText = findViewById(R.id.editTextPin)
        val checkBoxNewDevice: CheckBox = findViewById(R.id.checkBoxNewDevice)
        val checkBoxRememberPassword: CheckBox = findViewById(R.id.checkBoxRememberPassword)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            sendLoginMessage(
                ipAddress = textAddress.text.toString(),
                pin = Integer.parseInt(textPin.text.toString()),
                storage = storage,
                rememberPassword = checkBoxRememberPassword.isChecked,
                newDevice = checkBoxNewDevice.isChecked,
                storageFile = storageFile
            )

        }

    }

    private fun sendLoginMessage(
        ipAddress: String,
        pin: Int,
        storage: Storage,
        newDevice: Boolean,
        rememberPassword: Boolean,
        storageFile: File
    ) {
        val port = 5687
        launch(Dispatchers.IO) {
            var response: Response
            val factory = SSLSocketFactory.getDefault()
            val tempSocket = factory.createSocket() as SSLSocket
            var success: Boolean = false
            try {
                tempSocket.connect(InetSocketAddress(ipAddress, port), 1500)
                val message: String = Gson().toJson(LoginMessage("login", pin, newDevice))
                PrintWriter(tempSocket.outputStream, true).println(message)
                response = Gson().fromJson(
                    BufferedReader(InputStreamReader(tempSocket.inputStream)).readLine(),
                    Response::class.java
                )
                if (response.text.lowercase().contains("success")) {
                    storage.ipAddress = ipAddress
                    if (rememberPassword) {
                        storage.pin = pin
                        storage.token = response.internalMessage
                        storageFile.writeText(Gson().toJson(storage))
                    }
                    success = true
                }

            } catch (timeout: SocketTimeoutException) {
                timeout.printStackTrace()
                response = Response("Timeout! :c", "Not sent")
            } catch (exception: Exception) {
                exception.printStackTrace()
                response = Response(exception.message!!, "Not sent")
            }
            runOnUiThread {
                Toast.makeText(this@LoginActivity, response.text, Toast.LENGTH_SHORT).show()
                if (success) {
                    val intent = Intent(this@LoginActivity, OpenActivity::class.java).apply {
                        putExtra("ipAddress", ipAddress)
                        putExtra("token", response.internalMessage)
                    }

                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}