package com.example.android_app_todolist_simple.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android_app_todolist_simple.R
import kotlin.properties.Delegates


class AlarmReceiver:BroadcastReceiver() {
    private var uniqueId = SystemClock.uptimeMillis()
    override fun onReceive(context: Context, intent: Intent?) {
        // get To do string to show on notification
        val intentExtra = intent?.getStringExtra("Remind Todo")

        intent?.extras?.let {

            // to create notification channel with id first
            val builder = NotificationCompat.Builder(context,"bbb")
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle("Reminding")
                .setContentText(intentExtra)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(context)

            // need unique id
            notificationManager.notify(uniqueId.toInt(),builder.build())
        }
    }


}

