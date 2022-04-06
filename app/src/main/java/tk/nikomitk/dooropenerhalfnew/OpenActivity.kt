package tk.nikomitk.dooropenerhalfnew

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.*
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class OpenActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    // TODO discrete values, OTP stuff, logout button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)

        val ipAddress = intent.getStringExtra("ipAddress")!!
        val token = intent.getStringExtra("token")!!

        val openBar: SeekBar = findViewById(R.id.openBar)
        val openTimeTextView: TextView = findViewById(R.id.openTimeTextView)
        val openButton: Button = findViewById(R.id.openButton)
        val keypadSwitch: Switch = findViewById(R.id.keypadSwitch)
        val keypadBar: SeekBar = findViewById(R.id.keypadBar)
        val keypadTimeTextView: TextView = findViewById(R.id.keypadTimeTextView)
        val saveButton: Button = findViewById(R.id.saveButton)

        keypadBar.isEnabled = false
        saveButton.isEnabled = false

        openBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                openTimeTextView.text = (p0!!.progress + 1).toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        openButton.setOnClickListener {
            launch (Dispatchers.IO) {
                val response = sendMessage(
                    type = "open",
                    token = token,
                    content = openTimeTextView.text.toString(),
                    ipAddress = ipAddress
                )
                runOnUiThread{
                    Toast.makeText(this@OpenActivity, response.text, Toast.LENGTH_SHORT).show()
                }
                if(response.internalMessage.lowercase().contains("invalid token")) {
                    logout()
                }
            }
        }

        keypadSwitch.setOnCheckedChangeListener { p0, _ ->
            keypadBar.isEnabled = p0.isChecked
            saveButton.isEnabled = p0.isChecked
        }

        keypadBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                keypadTimeTextView.text = (p0!!.progress + 1).toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        saveButton.setOnClickListener {

        }
    }

    private suspend fun sendMessage(type: String, token: String, content: String, ipAddress: String): Response {
        val message = Message(type, token, content)
        val test: Deferred<Response> = coroutineScope {
            async {
                val socket = Socket()
                socket.connect(InetSocketAddress(ipAddress, 5687), 1500)
                PrintWriter(socket.getOutputStream(), true).println(Gson().toJson(message))
                return@async Gson().fromJson(
                    BufferedReader(InputStreamReader(socket.getInputStream())).readLine(),
                    Response::class.java
                )
            }
        }
        return test.await()
    }

    private fun logout(){
        // TODO(not implemented yet!)
    }
}