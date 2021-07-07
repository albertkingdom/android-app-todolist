package com.example.android_app_todolist_simple.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.databinding.FragmentAddEditBinding
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {
    val viewModel: MainViewModel by viewModels()
    val navigationArgs: AddEditFragmentArgs by navArgs()
    lateinit var _binding: FragmentAddEditBinding
    val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val binding = FragmentAddEditBinding.bind(view)
        _binding = FragmentAddEditBinding.bind(view)
        Log.d("args", navigationArgs.toString())
        val id = navigationArgs.id

        // edit existing to-do case
        if (id != -1) {
            lifecycle.coroutineScope.launch {

                viewModel.getSpecificTodo(id).collect {
                    binding.editText.setText(it.todoTitle)
                }
            }

            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                updateItem(id, newTodoTitle)
            }
        }
        // add new to-do case
        if (id == -1) {
            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                addItem(newTodoTitle)
            }
        }


    }

    private fun updateItem(id: Int, newTodoTitle: String) {
        viewModel.updateTodo(Todo(id = id, todoTitle = newTodoTitle, isChecked = false))
        val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
        findNavController().navigate(action)
    }

    private fun addItem(newTodoTitle: String) {
        viewModel.insertTodo(Todo(id = 0, todoTitle = newTodoTitle, isChecked = false))
        val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
        findNavController().navigate(action)
    }
}