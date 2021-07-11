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
        intent?.extras?.let {
            Toast.makeText(context, "wow", Toast.LENGTH_LONG).show()

            // to create notification channel with id first
            val builder = NotificationCompat.Builder(context,"bbb")
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle("Times up")
                .setContentText("Times up!!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(context)
            Log.d("alarm id", uniqueId.toInt().toString())
            // need unique id
            notificationManager.notify(uniqueId.toInt(),builder.build())
        }
    }


}

