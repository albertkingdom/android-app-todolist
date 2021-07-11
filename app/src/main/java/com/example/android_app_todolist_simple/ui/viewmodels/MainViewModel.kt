package com.example.android_app_todolist_simple.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

import javax.inject.Inject


//ViewModelInject is deprecated
@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
    val searchQuery = MutableStateFlow("")
    val hideCompleted = MutableStateFlow(false)
    var alarmTime:Long? = null
    // ViewModelScope is an extension property to the ViewModel class that automatically cancels its child coroutines when the ViewModel is destroyed.
    fun insertTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.insertTodo(todo)
    }


    fun getAllTodos() = combine(searchQuery, hideCompleted) { searchQuery, hideCompleted ->
        Pair(
            searchQuery,
            hideCompleted
        )
    }.flatMapLatest { (searchQuery, hideCompleted) ->
        mainRepository.getAllTodos(searchQuery, hideCompleted)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.updateTodo(todo)
    }

    fun getSpecificTodo(id: Int) = mainRepository.getSpecificTodo(id)

    fun delTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.delTodo(todo)
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(todoTitle: String): Boolean {
        if (todoTitle.isBlank()) {
            return false
        }
        return true
    }
}