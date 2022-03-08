package com.example.android_app_todolist_simple.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*

import javax.inject.Inject



@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
    companion object {
        val TAG = "MainViewModel"
    }
    val searchQuery = MutableLiveData("")

    val hideCompleted: MutableLiveData<Boolean> = MutableLiveData(false)


    // combine two liveData to one
    private val combinedQueryValues = MediatorLiveData<Pair<String?, Boolean?>>().apply {
        addSource(searchQuery) {
            value = Pair(it, hideCompleted.value)
        }
        addSource(hideCompleted) {
            value = Pair(searchQuery.value, it)
        }
    }
    // if combinedQueryValues is changed, it will execute the task in {}, which query the database
    private var allTodos: LiveData<MutableList<Todo>> = Transformations.switchMap(combinedQueryValues) {
        Log.d("VIEW MODEL searchQuery", it.first.toString())
        Log.d("VIEW MODEL hideComplete", it.second.toString())
        mainRepository.getAllTodos(searchQuery.value.toString(), hideCompleted.value!!).asLiveData()
    }

    var displaySpecificDayTodos: MutableLiveData<Boolean> = MutableLiveData(false)
    var selectedCalendarDay: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    // combine two liveData to one
    private val combinedFilterValues = MediatorLiveData<Pair<Calendar?, Boolean?>>().apply {
        addSource(selectedCalendarDay) {
            value = Pair(it, displaySpecificDayTodos.value)
        }

        addSource(displaySpecificDayTodos) {
            value = Pair(selectedCalendarDay.value, it)
        }
    }

    // if combinedFilterValues is changed, it will execute the task in {}, which filtering the allTodos
    var filteredTodoList: LiveData<List<Todo>> =
        Transformations.switchMap(combinedFilterValues) { pair ->
            Log.d("VIEW MODEL", "combinedFilterValues is ${combinedFilterValues.value}")

            Transformations.map(allTodos) { listOfTodos ->
                if (!pair.second!!) {
                    return@map listOfTodos
                }
                listOfTodos.filter { todo ->
                    (todo.createdTime != null) && (todo.createdTime > selectedCalendarDay.value?.timeInMillis!!) &&
                            (todo.createdTime < selectedCalendarDay.value?.timeInMillis!! + 86400000)
                }
            }

        }
    var todoId: MutableLiveData<Int> = MutableLiveData(0)
    var editTodoDetail = Transformations.switchMap(todoId) { id ->
        Log.d(TAG, "todo id is $id")
        mainRepository.getSpecificTodo(id).asLiveData()
    }

    // ViewModelScope is an extension property to the ViewModel class that automatically cancels its child coroutines when the ViewModel is destroyed.
    fun insertTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.insertTodo(todo)
    }



    fun updateTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.updateTodo(todo)
    }


    fun delTodo(todo: Todo) = viewModelScope.launch {
        mainRepository.delTodo(todo)
    }


}