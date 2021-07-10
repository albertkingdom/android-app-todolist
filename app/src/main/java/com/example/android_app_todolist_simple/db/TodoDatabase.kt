package com.example.android_app_todolist_simple.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

//@Database(entities = [Todo::class],version = 1,exportSchema = true)
//abstract class TodoDatabase:RoomDatabase() {
//    abstract fun getTodoDao():TodoDao
//}

@Database(entities = [Todo::class],version = 2, autoMigrations = [AutoMigration (from = 1, to = 2)],exportSchema = true)
abstract class TodoDatabase:RoomDatabase() {
    abstract fun getTodoDao():TodoDao
}