package tk.nikomitk.dooropenerhalfnew

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File

class WidgetNetworkService : Service(), CoroutineScope by MainScope() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //TODO opening doesn't stop
        val storage = File(applicationContext.filesDir, "storageFile").readText().toStorage()
        launch {
            NetworkUtil.sendMessage(
                Message(
                    "open",
                    storage.token!!,
                    "1"
                ).toJson(),
                storage.ipAddress!!
            )
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}