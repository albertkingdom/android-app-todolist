package com.example.android_app_todolist_simple.ui.viewmodels

import androidx.lifecycle.ViewModel


class AddEditViewModel: ViewModel() {
    private var _alarmTime: Long? = null
    var alarmTime: Long?
        get() = _alarmTime
        set(value) {
            _alarmTime = value
        }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(todoTitle: String): Boolean {
        if (todoTitle.isBlank()) {
            return false
        }
        return true
    }
}