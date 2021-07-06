package com.example.android_app_todolist_simple.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

import javax.inject.Inject


//ViewModelInject is deprecated
@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
    fun insertTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.insertTodo(todo)
    }



    suspend fun getAllTodos()=
        mainRepository.getAllTodos()



}