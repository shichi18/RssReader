package com.github.shichi18.rssreader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

private const val CHANNEL_ID = "update_channel"
private const val NOTIFICATION_ID = 1
private const val REQUEST_CODE = 1


fun createChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "新着",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        enableLights(false)
        enableVibration(true)
        setShowBadge(true)
    }

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}

fun notifyUpdate(context: Context){
    val intent = Intent(context,MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, REQUEST_CODE,intent,PendingIntent.FLAG_ONE_SHOT)

    //通知
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("記事が更新されました")
        .setContentText("新しい記事をチェックしましょう")
        .setContentIntent(pendingIntent)
        .setSmallIcon(R.drawable.ic_notification)
            
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(
        NOTIFICATION_ID,notification)
}
