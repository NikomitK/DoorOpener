package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.LoginMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //TODO add otp option

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO hash pin
        val storageFile = File(applicationContext.filesDir, "storageFile")

        if (intent.getBooleanExtra(getString(R.string.logout_extra), false)) {
            storageFile.writeText("")
        }

        var storage = Storage()

        if (!storageFile.createNewFile() && storageFile.readText().contains(":")) {
            storage = storageFile.readText().toStorage()
            startNextActivity(
                ipAddress = storage.ipAddress,
                token = storage.token,
            )
        }


        val textAddress: EditText = findViewById(R.id.editTextAddress)
        val textPin: EditText = findViewById(R.id.editTextPin)
        val checkBoxNewDevice: CheckBox = findViewById(R.id.checkBoxNewDevice)
        val checkBoxRememberPassword: CheckBox = findViewById(R.id.checkBoxRememberPassword)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            var success = false
            val ipAddress = textAddress.text.toString()
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    ipAddress = ipAddress,
                    message = LoginMessage(
                        type = "login",
                        pin = Integer.parseInt(textPin.text.toString()),
                        isNewDevice = checkBoxNewDevice.isChecked
                    ).toJson()
                )
                if (response.text.lowercase().contains(getString(R.string.success_internal))) {
                    storage.ipAddress = ipAddress
                    if (checkBoxRememberPassword.isChecked) {
                        storage.pin = Integer.parseInt(textPin.text.toString())
                        storage.token = response.internalMessage
                        storageFile.writeText(storage.toJson())
                    }
                    success = true
                }
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, response.text, Toast.LENGTH_SHORT)
                        .show()
                    if (success) {
                        startNextActivity(
                            ipAddress = ipAddress,
                            token = response.internalMessage,
                        )
                    }
                }
            }

        }

    }

    private fun startNextActivity(ipAddress: String?, token: String?) {
        val intent = Intent(this@LoginActivity, OpenActivity::class.java).apply {
            putExtra(getString(R.string.ipaddress_extra), ipAddress)
            putExtra(getString(R.string.token_extra), token)
        }
        startActivity(intent)
        finish()
    }

}