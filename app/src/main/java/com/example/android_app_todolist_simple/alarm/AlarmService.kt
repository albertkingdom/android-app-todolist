package com.example.android_app_todolist_simple.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import java.util.*


class AlarmService(private val context: Context) {

    var alarmMgr:AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // define what to do on alarm
    private lateinit var alarmIntent: PendingIntent



    fun setAlarm(c:Calendar, todoContent:String) {
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            //to make each intent unique, so each intent will push to broadcast
            intent.setAction(System.currentTimeMillis().toString())
            intent.putExtra("Remind Todo",todoContent)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmMgr?.setExact(
            AlarmManager.RTC_WAKEUP,
            c.timeInMillis,
            alarmIntent)



    }




}
/**   TODO: cancel alarm
 *
 */