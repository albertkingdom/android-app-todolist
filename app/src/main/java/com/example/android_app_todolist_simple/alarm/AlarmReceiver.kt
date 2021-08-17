package com.example.android_app_todolist_simple.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android_app_todolist_simple.R



class AlarmReceiver:BroadcastReceiver() {
    private var uniqueId = SystemClock.uptimeMillis()
    override fun onReceive(context: Context, intent: Intent?) {
        // get To do string to show on notification
        val intentExtra = intent?.getBundleExtra("REMIND_TODO")

        intent?.extras?.let {

            // to create notification channel with id first
            val builder = NotificationCompat.Builder(context,"test")
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle("Todo Reminding!")
                .setContentText(intentExtra?.getString("TODO_CONTENT"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(context)

            // need unique id
            notificationManager.notify(uniqueId.toInt(),builder.build())
        }
    }


}

