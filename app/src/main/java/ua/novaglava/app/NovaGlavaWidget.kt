package ua.novaglava.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class NovaGlavaWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it) }
    }
}

private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
    val prefs = context.getSharedPreferences("nova_glava", Context.MODE_PRIVATE)
    val title = prefs.getString("chapter_title", "Нова глава") ?: "Нова глава"
    val theme = prefs.getString("chapter_theme", "Почни свою главу") ?: "Почни свою главу"
    val frontline = prefs.getString("chapter_frontline", "Визнач свою Передову") ?: "Визнач свою Передову"
    val day = prefs.getInt("day", 1)
    val duration = prefs.getInt("duration", 30)

    val views = RemoteViews(context.packageName, R.layout.widget_nova_glava).apply {
        setTextViewText(R.id.widget_title, title)
        setTextViewText(R.id.widget_theme, theme)
        setTextViewText(R.id.widget_day, "День $day із $duration")
        setTextViewText(R.id.widget_frontline, frontline)

        val intent = Intent(context, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setOnClickPendingIntent(R.id.widget_root, pending)
    }
    manager.updateAppWidget(widgetId, views)
}

fun updateNovaGlavaWidgets(context: Context) {
    val manager = AppWidgetManager.getInstance(context)
    val component = ComponentName(context, NovaGlavaWidget::class.java)
    manager.getAppWidgetIds(component).forEach { updateWidget(context, manager, it) }
}
