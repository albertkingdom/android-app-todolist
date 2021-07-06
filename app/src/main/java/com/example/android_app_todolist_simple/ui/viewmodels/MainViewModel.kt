package com.example.android_app_todolist_simple.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.android_app_todolist_simple.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


//ViewModelInject is deprecated
@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {


}