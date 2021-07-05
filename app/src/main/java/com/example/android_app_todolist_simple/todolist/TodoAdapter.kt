package com.example.android_app_todolist_simple.todolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_app_todolist_simple.R

class TodoAdapter(private val todolist:MutableList<Todo>):RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val titleView = view.findViewById<TextView>(R.id.todoTitle)
        val isCheckView  = view.findViewById<CheckBox>(R.id.todoCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /**
         * bind the data and viewholder
         */
        val currentTodo = todolist[position]
        holder.titleView.text = todolist[position].title
        holder.isCheckView.isChecked = todolist[position].isChecked
        toggleStrikeThrough(holder.titleView, holder.isCheckView.isChecked)


        /**
         * TODO: add a onclick function to change line through
         */
        holder.isCheckView.setOnCheckedChangeListener { _, isChecked ->
            toggleStrikeThrough(holder.titleView, holder.isCheckView.isChecked)
            currentTodo.isChecked = !currentTodo.isChecked
        }
    }

    override fun getItemCount(): Int {
       return todolist.size
    }

    /**
     * add/ remove line through to todoTitle
     */
    private fun toggleStrikeThrough(todoTitle:TextView,isChecked:Boolean){
        // css: line through
        if(isChecked){
            todoTitle.paintFlags = todoTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            todoTitle.paintFlags = todoTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    fun addtodo(todo:Todo){
        todolist.add(todo)
        // update the recyclerview
        notifyItemInserted(todolist.size-1)

    }
    fun delDoneTodo(){
        todolist.removeAll { todo ->
            todo.isChecked
        }
        // update the recyclerview
        notifyDataSetChanged()
    }

}