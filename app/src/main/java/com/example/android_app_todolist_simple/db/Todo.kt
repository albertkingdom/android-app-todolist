package com.example.android_app_todolist_simple.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo (
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val todoTitle: String,
    val isChecked: Boolean
)