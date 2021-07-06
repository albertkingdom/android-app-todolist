package com.example.android_app_todolist_simple.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment: Fragment(R.layout.fragment_list){
    private val viewModel: MainViewModel by viewModels()
}