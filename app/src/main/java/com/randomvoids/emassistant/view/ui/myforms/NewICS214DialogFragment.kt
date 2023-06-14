package com.randomvoids.emassistant.view.ui.myforms

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.randomvoids.emassistant.databinding.Ics214DialogCreateNewIcs214Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewICS214DialogFragment : DialogFragment() {
    @VisibleForTesting val newICS214DialogViewModel: NewICS214DialogViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = Ics214DialogCreateNewIcs214Binding.inflate(layoutInflater).apply {
            lifecycleOwner = this@NewICS214DialogFragment
            viewModel = newICS214DialogViewModel.apply {
                loadIncident(-1)
            }
        }

        return AlertDialog.Builder(requireActivity()).setView(binding.root)
                .setPositiveButton("Create", DialogInterface.OnClickListener { _, _ ->
                    //TODO rework this workflow to make sense. also, it should launch the ics214 form dialog after this is done
                    val ics214Id = newICS214DialogViewModel.saveNewIncidentDialogData(requireContext())
                    val intent = Intent()
                    intent.putExtra("incidentId", ics214Id.toString())
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { _, _ ->
                        dialog?.cancel()
                    })
                .create()

    }
}