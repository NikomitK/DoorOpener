package tk.nikomitk.dooropenerhalfnew

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

class LogsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    lateinit var ipAddress: String
    lateinit var token: String
    lateinit var adapter: LogAdapter
    lateinit var logArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)
        val supportActBar:
                androidx.appcompat.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(supportActBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ipAddress = intent.getStringExtra("ipAddress")!!
        token = intent.getStringExtra("token")!!

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
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}