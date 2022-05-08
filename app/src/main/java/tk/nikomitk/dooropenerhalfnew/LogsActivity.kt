package tk.nikomitk.dooropenerhalfnew

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.thekhaeng.recyclerviewmargin.LinearLayoutMargin
import kotlinx.coroutines.*
import java.io.File

class LogsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var ipAddress: String
    private lateinit var token: String
    private lateinit var adapter: LogAdapter
    private lateinit var logArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ipAddress = intent.getStringExtra("ipAddress")!!
        token = intent.getStringExtra("token")!!

        val latestLogFile = File(applicationContext.filesDir, "latestLog.log")
        if(latestLogFile.exists() && latestLogFile.readText().isNotEmpty()) {
            logArray = Gson().fromJson(latestLogFile.readText(), Array<String>::class.java)
            adapter = LogAdapter(logArray)
            val recyclerView: RecyclerView = findViewById(R.id.logRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@LogsActivity)
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(LinearLayoutMargin(10))
            Toast.makeText(this, "Showing latest stored log", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.log_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_sync -> {
            launch(Dispatchers.IO) {
                val response = NetworkUtil.sendMessage(
                    type = "requestLogs",
                    token = token,
                    content = "",
                    ipAddress = ipAddress
                )
                if (response.internalMessage == "success") {
                    File(applicationContext.filesDir, "latestLog.log").writeText(response.text)
                    runOnUiThread {
                        logArray = Gson().fromJson(response.text, Array<String>::class.java)
                        adapter = LogAdapter(logArray)
                        val recyclerView: RecyclerView = findViewById(R.id.logRecyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this@LogsActivity)
                        recyclerView.adapter = adapter
                        recyclerView.addItemDecoration(LinearLayoutMargin(10))
                        Toast.makeText(
                            this@LogsActivity,
                            "Successfully synced logs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@LogsActivity,
                            response.text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if(response.internalMessage == "invalid token") {
                        logout()
                    }
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        startActivity(Intent(this, LoginActivity::class.java).putExtra("logout", true))
        OpenActivity.testlul.finish()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}