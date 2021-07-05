package com.example.android_app_todolist_simple.db

import androidx.room.*
import com.example.android_app_todolist_simple.todolist.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    suspend fun getAllTodos(): List<Todo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)
}