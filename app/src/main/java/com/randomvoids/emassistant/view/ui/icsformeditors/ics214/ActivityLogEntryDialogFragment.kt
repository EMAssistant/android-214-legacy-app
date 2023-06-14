package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.randomvoids.emassistant.databinding.DialogActivityLogEntryEditorBinding
import com.randomvoids.emassistant.view.ui.dialogs.TimePickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActivityLogEntryDialogFragment : DialogFragment() {
    @VisibleForTesting
    val activityLogEditorViewModel: ActivityLogEditorViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding =
            DialogActivityLogEntryEditorBinding.inflate(layoutInflater).apply {
                lifecycleOwner = this@ActivityLogEntryDialogFragment
                viewModel = activityLogEditorViewModel
                activityLogEntryTime.setOnClickListener {
                    val timePicker = TimePickerDialogFragment(activityLogEditorViewModel.activityLogEntryEditorLiveData.value?.time)
                    timePicker.setTargetFragment(this@ActivityLogEntryDialogFragment, 1234)
                    timePicker.show(parentFragmentManager, "ActivityLogEntryTimePicker")
                }
            }
        return activity?.let {
            AlertDialog.Builder(it).setView(binding.root)
                // Add action buttons
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { _, _ ->
                        activityLogEditorViewModel.saveActiveActivityLogEntryEdit()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { _, _ ->
                        dialog?.cancel()
                    })
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1234 -> {
                activityLogEditorViewModel.updateLogEntryEditorDate(data!!.getSerializableExtra("selectedTime") as Date)
            }
        }
    }
}