package com.example.android_app_todolist_simple.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android_app_todolist_simple.ui.viewmodels.DelCompletedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DelCompletedFragment: DialogFragment() {
    val viewModel: DelCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage("Do you want to delete all completed todos?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.delCompletedTodos()
                })
            .setNegativeButton("Cancel",
                null)
            .create()

    }
}