package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage

class OpenActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        lateinit var thisActivity: OpenActivity
        lateinit var globalIpAddress: String
        lateinit var globalToken: String
    }

    // TODO discrete values, OTP stuff
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        thisActivity = this
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)

        val ipAddress = intent.getStringExtra("ipAddress")!!
        val token = intent.getStringExtra("token")!!
        globalIpAddress = ipAddress
        globalToken = token

        val openBar: SeekBar = findViewById(R.id.openBar)
        val openTimeTextView: TextView = findViewById(R.id.openTimeTextView)
        val openButton: Button = findViewById(R.id.openButton)
        val otpAddressText: EditText = findViewById(R.id.textOnetimeAddress)
        val otpPinText: EditText = findViewById(R.id.textOnetimePin)
        val otpUseButton: Button = findViewById(R.id.buttonUseOtp)
        val keypadSwitch: Switch = findViewById(R.id.keypadSwitch)
        val keypadBar: SeekBar = findViewById(R.id.keypadBar)
        val keypadTimeTextView: TextView = findViewById(R.id.keypadTimeTextView)
        val saveButton: Button = findViewById(R.id.saveButton)

        keypadBar.isEnabled = false

        openBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                (p0!!.progress + 1).toString().also { openTimeTextView.text = it }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        openButton.setOnClickListener {
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    type = "open",
                    token = token,
                    content = openTimeTextView.text.toString(),
                    ipAddress = ipAddress
                )
                runOnUiThread {
                    Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                }
                if (response.internalMessage.lowercase().contains("invalid token")) {
                    logout()
                }
            }
        }

        otpUseButton.setOnClickListener {
            if (otpAddressText.text.isNotEmpty() && otpPinText.text.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    val response = sendMessage(
                        type = "open",
                        token = otpPinText.text.toString(),
                        content = openTimeTextView.text.toString(),
                        ipAddress = otpAddressText.text.toString()
                    )
                    runOnUiThread {
                        Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                    }
                    if(response.internalMessage == "success") otpPinText.setText("")
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

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        saveButton.setOnClickListener {
            launch(Dispatchers.IO) {
                val response = sendMessage(
                    type = "keypadConfig",
                    token = token,
                    content = if (keypadSwitch.isChecked) keypadTimeTextView.text.toString() else (-1).toString(),
                    ipAddress = ipAddress
                )
                runOnUiThread {
                    Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                }
                if (response.internalMessage.lowercase().contains("invalid token")) {
                    logout()
                }
            }
        }
    }

    private fun logout() {
        startActivity(Intent(this, LoginActivity::class.java).putExtra("logout", true))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.resource_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra("ipAddress", globalIpAddress)
                putExtra("token", globalToken)
            })
            true
        }

        R.id.action_log -> {
            startActivity(
                Intent(this, LogsActivity::class.java).putExtra(
                    "ipAddress",
                    globalIpAddress
                ).putExtra("token", globalToken)
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