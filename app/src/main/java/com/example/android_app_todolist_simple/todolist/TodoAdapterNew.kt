package com.example.android_app_todolist_simple.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.db.Todo

class TodoAdapterNew(): ListAdapter<Todo, TodoAdapterNew.TodoViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }
 class TodoViewHolder(view: View):RecyclerView.ViewHolder(view){
     val titleView = view.findViewById<TextView>(R.id.todoTitle)
     val isCheckView  = view.findViewById<CheckBox>(R.id.todoCheck)
 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var currentTodo = getItem(position)
        holder.titleView.text = currentTodo.todoTitle
        holder.isCheckView.isChecked = currentTodo.isChecked
//        toggleStrikeThrough(holder.titleView, holder.isCheckView.isChecked)
    }


}