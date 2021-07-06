package com.example.android_app_todolist_simple.repositories

import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.db.TodoDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo)=todoDao.insert(todo)

    suspend fun getAllTodos() = todoDao.getAllTodos()

    suspend fun delTodo(todo: Todo) = todoDao.delete(todo)
}