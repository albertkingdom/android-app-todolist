package com.example.android_app_todolist_simple.repositories

import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.db.TodoDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo)=todoDao.insert(todo)

    fun getAllTodos(queryString: String, hideCompleted: Boolean) = todoDao.getAllTodos(queryString, hideCompleted)

    suspend fun delTodo(todo: Todo) = todoDao.delete(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.update(todo)

    fun getSpecificTodo(id:Int) = todoDao.getSpecificTodo(id)

    suspend fun delAllCompletedTodo() = todoDao.delAllCompletedTodo()

}