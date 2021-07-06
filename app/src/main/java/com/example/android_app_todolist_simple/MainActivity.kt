package com.example.android_app_todolist_simple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    lateinit var todoAdapter: TodoAdapter
//    @Inject
//    lateinit var todoDAO: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Log.d("runDao", "RUNDAO: ${todoDAO.hashCode()}")

        // recycler view setting
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
//
//        val todolist: MutableList<Todo> = mutableListOf()
//        todoAdapter = TodoAdapter(todolist)
//        recyclerView.adapter = todoAdapter
//
//
//        // button [addtodo] onClick
//        findViewById<Button>(R.id.btnAddtodo).setOnClickListener {
//            val todoTitle = findViewById<EditText>(R.id.newTodo).text
//
//            todoAdapter.addtodo(Todo(todoTitle.toString()))
//
//            todoTitle.clear()
//
//        }
//
//        // button [del donetodo] onCLick
//        findViewById<Button>(R.id.btnDeldonetodo).setOnClickListener {
//            todoAdapter.delDoneTodo()
//        }





    }
}