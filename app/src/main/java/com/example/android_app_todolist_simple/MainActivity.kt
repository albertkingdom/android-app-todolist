package com.example.android_app_todolist_simple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.android_app_todolist_simple.db.TodoDao
import com.example.android_app_todolist_simple.db.TodoDatabase
import com.example.android_app_todolist_simple.todolist.Todo
import com.example.android_app_todolist_simple.todolist.TodoAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var todoAdapter: TodoAdapter
//    @Inject
//    lateinit var todoDAO: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Log.d("runDao", "RUNDAO: ${todoDAO.hashCode()}")

        // recycler view setting
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        val todolist: MutableList<Todo> = mutableListOf()
        todoAdapter = TodoAdapter(todolist)
        recyclerView.adapter = todoAdapter


        // button [addtodo] onClick
        findViewById<Button>(R.id.btnAddtodo).setOnClickListener {
            val todoTitle = findViewById<EditText>(R.id.newTodo).text

            todoAdapter.addtodo(Todo(todoTitle.toString()))

            todoTitle.clear()

        }

        // button [del donetodo] onCLick
        findViewById<Button>(R.id.btnDeldonetodo).setOnClickListener {
            todoAdapter.delDoneTodo()
        }





    }
}