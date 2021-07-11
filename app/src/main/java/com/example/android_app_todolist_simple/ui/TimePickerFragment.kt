package com.example.android_app_todolist_simple.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.android_app_todolist_simple.alarm.AlarmService
import java.util.*

class TimePickerFragment(val callback:(Calendar)->Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,hourOfDay)
        c.set(Calendar.MINUTE, minute)

        //start the alarm
        AlarmService(requireContext()).setAlarm(c)

        /**
         * show the alarm time on screen
         */
        try {
//           view.findViewById<TextView>(R.id.alarmTimeText).text = SimpleDateFormat("HH:mm:ss").format(c.time)
            callback(c)

        }catch (e:Exception){
            Log.d("error",e.toString())
        }
    }


}