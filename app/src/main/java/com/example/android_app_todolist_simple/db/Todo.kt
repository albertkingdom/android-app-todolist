package com.example.android_app_todolist_simple.db

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "todos")
@Parcelize
data class Todo (
    @PrimaryKey(autoGenerate = true) val id:Int,
    val todoTitle: String,
    val todoDetail: String? = null,
    var isChecked: Boolean,
    val alarmTime: Long? = null, //new column in version2
    val audioRecord: String? = null
):Parcelable