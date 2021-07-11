package com.example.android_app_todolist_simple.ui.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.db.TodoDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

//@HiltViewModel
//class AddEditViewModel @Inject constructor(
//    private val todoDao: TodoDao,
//    private val state:SavedStateHandle):ViewModel() {
//
//    val todo = state.get<Todo>("todo")
//
//    var todoTitle = state.get<String>("todoTitle") ?: todo?.todoTitle ?: ""
//        set(value) {
//            field = value
//            state.set("todoTitle", value)
//        }
//
//
//}