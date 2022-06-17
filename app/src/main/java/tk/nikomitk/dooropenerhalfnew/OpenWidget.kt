package tk.nikomitk.dooropenerhalfnew

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
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
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object

//    val pendingIntent: PendingIntent = PendingIntent.getActivity(
//        /* context = */ context,
//        /* requestCode = */  0,
//        /* intent = */ Intent(context, WidgetNetworkService::class.java).apply {
//
//        },
//        /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
    val pendingIntent = PendingIntent.getService(
        context,
        0,
        Intent(context, WidgetNetworkService::class.java).also {
            context.startService(it)},
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val views = RemoteViews(context.packageName, R.layout.open_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
    views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}