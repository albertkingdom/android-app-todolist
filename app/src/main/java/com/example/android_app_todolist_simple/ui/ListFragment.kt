package com.example.android_app_todolist_simple.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.todolist.Todo_not
import com.example.android_app_todolist_simple.todolist.TodoAdapter
import com.example.android_app_todolist_simple.todolist.TodoAdapterNew
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel: MainViewModel by viewModels()
    lateinit var todoAdapter: TodoAdapterNew


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view setting
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)

        /**
         * pass a func to adapter
         */
        todoAdapter = TodoAdapterNew {
            lifecycle.coroutineScope.launch { completeTodo(it) }
        }

        recyclerView.adapter = todoAdapter


        // button [addtodo] onClick

        lifecycle.coroutineScope.launch {
            viewModel.getAllTodos().collect {
                todoAdapter.submitList(it)
            }
        }


        view.findViewById<Button>(R.id.btnAddtodo).setOnClickListener {
            val todoTitle = view.findViewById<EditText>(R.id.newTodo).text
            println("new todo: $todoTitle")
            /**
             * id:0 will auto increment
             */
            viewModel.insertTodo(Todo(0, todoTitle.toString(), false))

            todoTitle.clear()
        }

    }

    private suspend fun completeTodo(todo:Todo){
        viewModel.updateTodo(todo)
    }
}