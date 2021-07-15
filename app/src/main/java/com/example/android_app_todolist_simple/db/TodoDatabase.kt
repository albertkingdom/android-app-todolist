package com.example.android_app_todolist_simple.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

//@Database(entities = [Todo::class],version = 1,exportSchema = true)
//abstract class TodoDatabase:RoomDatabase() {
//    abstract fun getTodoDao():TodoDao
//}

@Database(entities = [Todo::class],version = 3, autoMigrations = [AutoMigration (from = 2, to = 3)],exportSchema = true)
abstract class TodoDatabase:RoomDatabase() {
    abstract fun getTodoDao():TodoDao
}