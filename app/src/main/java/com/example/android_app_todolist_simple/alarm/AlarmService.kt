package com.example.android_app_todolist_simple.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle

import java.util.*


class AlarmService(private val context: Context) {

    var alarmMgr:AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // define what to do on alarm
    private lateinit var alarmIntent: PendingIntent



    fun setAlarm(c:Calendar, todoBundle: Bundle) {
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->

            intent.putExtra("REMIND_TODO", todoBundle)
            // 2nd variable(request code) is to make each intent unique
            PendingIntent.getBroadcast(context, todoBundle.getInt("TODO_ID"), intent, 0)
        }
        // To set alarm Only if the alarm time is after current time
        if(c.after(Calendar.getInstance())) {
            alarmMgr?.setExact(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis,
                alarmIntent
            )
        }


    }
    fun cancelAlarm(todoBundle: Bundle){
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->

            intent.putExtra("REMIND_TODO", todoBundle)
            PendingIntent.getBroadcast(context, todoBundle.getInt("TODO_ID"), intent, 0)
        }
        alarmMgr?.cancel(alarmIntent)

    }
}
