package com.clipsaver.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class DownloadNotifier(private val context: Context) {

    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "clipsaver_downloads"
        const val CHANNEL_NAME = "تنزيلات كليب سيفر"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات حالة تنزيل المقاطع الصوتية والمرئية"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun showDownloading(title: String, progressFraction: Float) {
        val pct = (progressFraction * 100f).toInt().coerceIn(0, 100)
        val indeterminate = progressFraction <= 0f
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("جاري التحميل")
            .setContentText(if (indeterminate) title else "$title • $pct%")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, pct, indeterminate)

        runCatching { manager.notify(NOTIFICATION_ID, builder.build()) }
    }

    fun showCompleted(saved: SavedFile) {
        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(saved.uri, saved.mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("تم التحميل")
            .setContentText(saved.name)
            .setAutoCancel(true)
            .setOngoing(false)
            .setOnlyAlertOnce(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, false)
            .setContentIntent(pendingIntent)

        runCatching { manager.notify(NOTIFICATION_ID, builder.build()) }
    }

    fun showFailed(reason: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("تعذر التحميل")
            .setContentText(reason.lineSequence().firstOrNull().orEmpty().ifBlank { "فشل التنزيل" })
            .setAutoCancel(true)
            .setOngoing(false)
            .setProgress(0, 0, false)

        runCatching { manager.notify(NOTIFICATION_ID, builder.build()) }
    }

    fun cancel() {
        runCatching { manager.cancel(NOTIFICATION_ID) }
    }
}
