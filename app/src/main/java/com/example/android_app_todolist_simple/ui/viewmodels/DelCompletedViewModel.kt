package com.example.android_app_todolist_simple.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app_todolist_simple.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DelCompletedViewModel  @Inject constructor(val mainRepository: MainRepository) : ViewModel() {

    fun delCompletedTodos() = viewModelScope.launch {
        mainRepository.delAllCompletedTodo()

    }

}