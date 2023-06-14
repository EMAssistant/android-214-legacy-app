package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.randomvoids.emassistant.databinding.Ics214DialogFragmentResourceItemEditorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ICS214ResourceDialogFragment : DialogFragment() {
    @VisibleForTesting
    val ics214ResourcesListViewModel : ICS214ResourcesListViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding =
            Ics214DialogFragmentResourceItemEditorBinding.inflate(layoutInflater).apply {
                lifecycleOwner = this@ICS214ResourceDialogFragment
                viewModel = ics214ResourcesListViewModel
            }
        return activity?.let {
            AlertDialog.Builder(it).setView(binding.root)
                // Add action buttons
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { _, _ ->
                        ics214ResourcesListViewModel.saveActiveResourceItemEdit()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { _, _ ->
                        dialog?.cancel()
                    })
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}