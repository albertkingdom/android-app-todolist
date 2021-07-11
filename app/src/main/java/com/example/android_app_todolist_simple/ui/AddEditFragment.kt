package com.example.android_app_todolist_simple.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.*

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
                    //show alarm time
                    binding.alarmText.text = SimpleDateFormat( "h:mm a").format(Date(it.alarmTime!!))
                }
            }

            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                updateItem(id, newTodoTitle)
            }
        }
        // add new to-do case
        if (id == -1) {
            viewModel.alarmTime = null
            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                addItem(newTodoTitle)
            }
        }


        //click to pick time and start the alarm
        _binding.picktime.setOnClickListener {
            fun showTimePickerDialog(v: View) {
                TimePickerFragment(::updateAlarmTimeText).show(childFragmentManager, "timePicker")
            }
            showTimePickerDialog(it)
        }

    }

    private fun updateItem(id: Int, newTodoTitle: String) {
        if (viewModel.isEntryValid(newTodoTitle)){
            viewModel.updateTodo(Todo(id = id, todoTitle = newTodoTitle, isChecked = false, alarmTime = viewModel.alarmTime))
            val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }

    private fun addItem(newTodoTitle: String) {
        if(viewModel.isEntryValid(newTodoTitle)){
            viewModel.insertTodo(Todo(id = 0, todoTitle = newTodoTitle, isChecked = false,alarmTime = viewModel.alarmTime ))
            val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }
    private fun updateAlarmTimeText(c: Calendar?){
        _binding.alarmText.text = SimpleDateFormat("h:mm a").format(c?.time)
        viewModel.alarmTime = c?.timeInMillis

        Log.d("alarm time",viewModel.alarmTime.toString())
        Log.d("alarm time 2", c?.timeInMillis.toString())
        Log.d("alarm convert ms to",SimpleDateFormat( "h:mm a").format(Date(c?.timeInMillis!!)))
    }
}