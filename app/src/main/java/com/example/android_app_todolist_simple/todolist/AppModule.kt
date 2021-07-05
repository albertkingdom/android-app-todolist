package com.example.android_app_todolist_simple.todolist

import android.content.Context
import androidx.room.Room
import com.example.android_app_todolist_simple.db.TodoDatabase
import com.example.android_app_todolist_simple.others.Constants.TODO_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object AppModule  {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        TodoDatabase::class.java,
        TODO_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: TodoDatabase) = db.getTodoDao()
}
