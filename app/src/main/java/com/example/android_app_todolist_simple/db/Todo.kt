package com.example.android_app_todolist_simple.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "todos")
@Parcelize
data class Todo (
    @PrimaryKey(autoGenerate = true) val id:Int,
    val todoTitle: String,
    var isChecked: Boolean
):Parcelable