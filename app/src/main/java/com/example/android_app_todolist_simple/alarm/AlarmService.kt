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


    // Set the alarm to start at approximately 2:00 p.m.
//    private val calendar: Calendar = Calendar.getInstance().apply {
//        timeInMillis = System.currentTimeMillis()
//        set(Calendar.HOUR_OF_DAY, 14)
//        set(Calendar.MINUTE,30)
//    }


// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
    fun setAlarm(c:Calendar) {
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            //to make each intent unique, so each intent will push to broadcast
            intent.setAction(System.currentTimeMillis().toString())
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