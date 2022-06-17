package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File

class OpenActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //TODO make scrollable for smaller phones
    companion object {
        lateinit var globalIpAddress: String
        lateinit var globalToken: String
        lateinit var storageFile: File
        lateinit var storage: Storage
        lateinit var thisActivity: OpenActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)

        val ipAddress = intent.getStringExtra(getString(R.string.ipaddress_extra))!!
        val token = intent.getStringExtra(getString(R.string.token_extra))!!
        globalIpAddress = ipAddress
        globalToken = token
        storageFile = File(applicationContext.filesDir, "storageFile")
        storage = Gson().fromJson(storageFile.readText(), Storage::class.java)
        thisActivity = this

        val openBar: SeekBar = findViewById(R.id.openBar)
        val openTimeTextView: TextView = findViewById(R.id.openTimeTextView)
        val openButton: Button = findViewById(R.id.openButton)
        val otpAddressText: EditText = findViewById(R.id.textOnetimeAddress)
        val otpPinText: EditText = findViewById(R.id.textOnetimePin)
        val otpUseButton: Button = findViewById(R.id.buttonUseOtp)
        val keypadSwitch: Switch = findViewById(R.id.keypadSwitch)
        val keypadBar: SeekBar = findViewById<SeekBar?>(R.id.keypadBar).apply { isEnabled = false }
        val keypadTimeTextView: TextView = findViewById(R.id.keypadTimeTextView)
        val saveButton: Button = findViewById(R.id.saveButton)


        openBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                (p0!!.progress + 1).toString().also { openTimeTextView.text = it }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { /* not needed */ }

            override fun onStopTrackingTouch(p0: SeekBar?) { /* not needed */ }
        })

        openButton.setOnClickListener {
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    message = Message(
                        type = getString(R.string.open_type),
                        token = token,
                        content = openTimeTextView.text.toString()
                    ).toJson(),
                    ipAddress = ipAddress
                )
                runOnUiThread {
                    Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                }
                if (response.internalMessage.lowercase()
                        .contains(getString(R.string.invalid_token_internal))
                ) {
                    logout()
                }
            }
        }

        otpUseButton.setOnClickListener {
            if (otpAddressText.text.isNotEmpty() && otpPinText.text.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        message = Message(
                            type = getString(R.string.otp_open_type),
                            token = otpPinText.text.toString(),
                            content = openTimeTextView.text.toString()
                        ).toJson(),
                        ipAddress = otpAddressText.text.toString(),
                    )
                    runOnUiThread {
                        Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                        if (response.internalMessage == getString(R.string.success_internal)) otpPinText.setText(
                            ""
                        )
                    }
                }
            }
        }

        keypadSwitch.setOnCheckedChangeListener { p0, _ ->
            keypadBar.isEnabled = p0.isChecked
        }

        keypadBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                (p0!!.progress + 1).toString().also { keypadTimeTextView.text = it }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { /* not needed */
            }

            override fun onStopTrackingTouch(p0: SeekBar?) { /* not needed */
            }
        })

        saveButton.setOnClickListener {
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    message = Message(
                        type = getString(R.string.configure_keypad_type),
                        token = token,
                        content = if (keypadSwitch.isChecked) keypadTimeTextView.text.toString() else (-1).toString()
                    ).toJson(),
                    ipAddress = ipAddress
                )
                runOnUiThread {
                    Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                }
                if (response.internalMessage.lowercase()
                        .contains(getString(R.string.invalid_token_internal))
                ) {
                    logout()
                }
            }
        }
    }

    private fun logout() {
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            ).putExtra(getString(R.string.logout_extra), true)
        )
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.resource_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra(getString(R.string.ipaddress_extra), globalIpAddress)
                putExtra(getString(R.string.token_extra), globalToken)
            })
            true
        }

        R.id.action_log -> {
            startActivity(
                Intent(this, LogsActivity::class.java).putExtra(
                    getString(R.string.ipaddress_extra),
                    globalIpAddress
                ).putExtra(getString(R.string.token_extra), globalToken)
            )
            true
        }

        R.id.action_otp -> {
            startActivity(Intent(this, OTPActivity::class.java))
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}