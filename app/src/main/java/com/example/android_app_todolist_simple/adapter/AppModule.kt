package com.example.android_app_todolist_simple.adapter

import android.content.Context
import androidx.room.Room
import com.example.android_app_todolist_simple.db.TodoDatabase
import com.example.android_app_todolist_simple.others.Constants.TODO_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {
    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationCompenent (i.e. everywhere in the application)
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        TodoDatabase::class.java,
        TODO_DATABASE_NAME
    ).build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoDatabase) = db.getTodoDao() // The reason we can implement a Dao for the database
}
