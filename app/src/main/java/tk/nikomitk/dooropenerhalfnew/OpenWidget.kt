package tk.nikomitk.dooropenerhalfnew

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.runBlocking
import tk.nikomitk.dooropenerhalfnew.messagetypes.Message
import tk.nikomitk.dooropenerhalfnew.messagetypes.Response
import tk.nikomitk.dooropenerhalfnew.messagetypes.toJson
import java.io.File

const val ACTION_OPEN = "tk.nikomitk.dooropenerhalfnew.OPEN"

class OpenWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        //if the intent contains this string, the received broadcast is relevant for this action
        if (intent!!.action == ACTION_OPEN) {
            val response: Response
            runBlocking {
                val storage = File(context!!.filesDir, "storageFile").readText().toStorage()
                response = NetworkUtil.sendMessage(
                    Message(
                        "open",
                        storage.token!!,
                        storage.widgetTime.toString()
                    ).toJson(),
                    storage.ipAddress!!
                )
            }
            response.text.toast(context!!)
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    // Broadcast an intent with the ACTION_OPEN string, so the receiver can do it's work
    val intent = Intent(context, OpenWidget::class.java)
    intent.action = ACTION_OPEN
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val views = RemoteViews(context.packageName, R.layout.open_widget)
    views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}