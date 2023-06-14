package com.randomvoids.emassistant.view.ui.profile.mileagelog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.VisibleForTesting
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.randomvoids.emassistant.databinding.DialogMileageLogEntryEditorBinding
import com.randomvoids.emassistant.extensions.addOnPropertyChanged
import com.randomvoids.emassistant.view.ui.dialogs.DatePickerFragment
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MileageLogEntryDialogFragment : DialogFragment() {
    @VisibleForTesting val mileageLogViewModel: MileageLogViewModel by activityViewModels()
    private lateinit var callback: Observable.OnPropertyChangedCallback

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogMileageLogEntryEditorBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MileageLogEntryDialogFragment
            viewModel = mileageLogViewModel
            mileageLogDriveDate.setOnClickListener {
                val datePicker = DatePickerFragment(mileageLogViewModel.mileageLogEntryLiveData.value!!.driveDate)
                datePicker.setTargetFragment(this@MileageLogEntryDialogFragment, 1234)
                datePicker.show(parentFragmentManager, "MileageLogDriveDateDatePicker")
            }
        }
        return AlertDialog.Builder(requireActivity()).setView(binding.root)
            .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                mileageLogViewModel.saveActiveMileageLogEntry()
            })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { _, _ ->
                    dialog?.cancel()
                })
            .create()
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog
        d.let { itMain ->
            itMain.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            val positiveButton = itMain.getButton(Dialog.BUTTON_POSITIVE)
            mileageLogViewModel.mileageLogEntryLiveData.value.whatIfNotNull(
                whatIf = { positiveButton.isEnabled = (it.mileageLogEntryId > 0)},
                whatIfNot = { positiveButton.isEnabled = false }
            )
            callback = mileageLogViewModel.isValid.addOnPropertyChanged {
                positiveButton.isEnabled = it.get()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mileageLogViewModel.isValid.removeOnPropertyChangedCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1234 -> {
                mileageLogViewModel.updateMileageLogEntryEditorDate(data!!.getSerializableExtra("selectedDate") as Date)
            }
        }
    }
}