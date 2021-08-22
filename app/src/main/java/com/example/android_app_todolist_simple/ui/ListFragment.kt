package com.example.android_app_todolist_simple.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.databinding.FragmentListBinding
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.todolist.TodoAdapterNew
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel: MainViewModel by viewModels()
    lateinit var todoAdapter: TodoAdapterNew
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    val events: MutableList<EventDay> = ArrayList() //events list for calendar
    var withInRangeTodoList = listOf<Todo>() // filtered todos list for calendar
    var allTodoList = listOf<Todo>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)
        // recycler view setting
        val recyclerView: RecyclerView = binding.recyclerview

        /**
         * pass lambda functions to adapter
         */
        todoAdapter = TodoAdapterNew(
            onItemClicked = ::completeTodo,
            onEditTodo = ::editTodo
        )

        recyclerView.adapter = todoAdapter



        // get all todos from db
        lifecycle.coroutineScope.launch {
            viewModel.getAllTodos().collect {
                todoAdapter.submitList(it)
                allTodoList = it

                /** Add icon to calendar if the specific date have todos
                 *
                  */
                withContext(Dispatchers.Default) {

                    for (todo in it) {
                        val calendar = Calendar.getInstance()
                        if (todo.createdTime != null) calendar.timeInMillis = todo.createdTime

                        events.add(EventDay(calendar, R.drawable.ic_baseline_star))

                    }
                }

                binding.calendarView.setEvents(events)
            }

        }


        // click button to navigate to add_edit fragment
        binding.addTask.setOnClickListener {
            findNavController().navigate(
                ListFragmentDirections.actionListFragmentToAddEditFragment(
                    -1
                )
            )
        }
        //show menu
        setHasOptionsMenu(true)

        // swipe to delete
        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val todo = todoAdapter.currentList[viewHolder.absoluteAdapterPosition]
                viewModel.delTodo(todo)

                //undo delete operation
                Snackbar.make(requireView(),"one todo is deleted !", Snackbar.LENGTH_LONG).setAction("Undo"){
                    viewModel.insertTodo(todo)
                }.show()
            }
        }).attachToRecyclerView(recyclerView)

        /**
         * click calendar date to display todos created on that date
         */
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {

                val clickedDay = eventDay.calendar

                lifecycle.coroutineScope.launch {
                    try {
                        viewModel.getAllTodos().collect {
                            withContext(Dispatchers.Default){
                                withInRangeTodoList = it.filter { todo ->
                                    (todo.createdTime != null) && (todo.createdTime > clickedDay.timeInMillis) &&
                                            (todo.createdTime < clickedDay.timeInMillis + 86400000)

                                }
                            }
                            if (binding.switchButton.isChecked) {
                                todoAdapter.submitList(withInRangeTodoList)
                            }else{
                                todoAdapter.submitList(it)
                            }
                        }
                    } catch (e: Throwable) {
                        Log.e("calendar error", e.toString())
                    }

                }
            }

        })

        binding.switchButton.setOnCheckedChangeListener { button, isChecked ->

            if(isChecked){
                todoAdapter.submitList(withInRangeTodoList)
            }else{
                todoAdapter.submitList(allTodoList)
            }
        }

    }


    // menu
    // https://developer.android.com/training/appbar/action-views#action-view
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)

        val searchView = searchItem.actionView as SearchView


        //get search query input and do something
        //https://stackoverflow.com/questions/55949305/how-to-properly-retrieve-data-from-searchview-in-kotlin
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchQuery.value = newText
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return true
            }
        })

    }

    // click menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.switchView -> {
                if(item.title == "calendarView"){
                    item.title = "listView"
                    item.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_list, null)
                    binding.calendarView.visibility = View.GONE
                }else{
                    item.title = "calendarView"
                    item.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_calendar, null)
                    binding.calendarView.visibility = View.VISIBLE
                }

                true
            }
            R.id.action_hide_completed -> {
                // to change checked to unchecked or vice versa
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.action_del_all_completed -> {
                findNavController().navigate(R.id.delCompletedFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun completeTodo(todo: Todo) {
        viewModel.updateTodo(todo)
    }

    private fun editTodo(todo: Todo) {
        val action = ListFragmentDirections.actionListFragmentToAddEditFragment(todo.id)
        findNavController().navigate(action)
    }


}