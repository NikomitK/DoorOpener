package tk.nikomitk.dooropenerhalfnew

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.NetworkUtil.sendMessage
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class OTPActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var storage: Storage
    private lateinit var storageFile: File
    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        val supportActBar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        storageFile = File(applicationContext.filesDir, "storageFile")
        storage = Gson().fromJson(storageFile.readText(), Storage::class.java)

        adapter = CustomAdapter(storage.otps, this)
        val recyclerView: RecyclerView = findViewById(R.id.logRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addOtpButton: FloatingActionButton = findViewById(R.id.addOtpButton)
        addOtpButton.setOnClickListener {
            launch(Dispatchers.IO) {
                val newOtp = Otp(
                    Random(LocalTime.now().nano)
                        .nextInt(1000, 999999).toString(),
                    LocalDate.now().plusMonths(1).toString()
                )
                val response = sendMessage(
                    message = Message(
                        type = getString(R.string.add_otp_type),
                        token = storage.token!!,
                        content = Gson().toJson(newOtp)
                    ).toJson(),
                    ipAddress = storage.ipAddress!!
                )
                runOnUiThread {
                    Toast.makeText(
                        this@OTPActivity, response.text, Toast.LENGTH_SHORT
                    ).show()
                    if (response.internalMessage == getString(R.string.success_internal)) {
                        storage.otps.add(
                            newOtp
                        )
                        storageFile.writeText(Gson().toJson(storage))
                        adapter.notifyItemInserted(storage.otps.size - 1)
                    }
                }
            }
        }
    }

    fun removeOtp(position: Int) {
        launch(Dispatchers.IO) {
            val response = sendMessage(
                message = Message(
                    type = getString(R.string.remove_otp_type),
                    token = storage.token!!,
                    content = Gson().toJson(storage.otps[position])
                ).toJson(),
                ipAddress = storage.ipAddress!!
            )
            runOnUiThread {
                Toast.makeText(this@OTPActivity, response.text, Toast.LENGTH_SHORT).show()
                if (response.internalMessage == getString(R.string.success_internal)) {
                    storage.otps.removeAt(position)
                    adapter.notifyDataSetChanged()
                    storageFile.writeText(Gson().toJson(storage))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}