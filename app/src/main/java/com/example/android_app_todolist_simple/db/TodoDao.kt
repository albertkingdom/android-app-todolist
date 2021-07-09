package com.example.android_app_todolist_simple.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE todoTitle LIKE '%' || :searchQuery || '%'")
    fun getAllTodos(searchQuery: String): Flow<MutableList<Todo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getSpecificTodo(id:Int):Flow<Todo>

    @Query("DELETE FROM todos WHERE isChecked = 1")
    suspend fun delAllCompletedTodo()

}