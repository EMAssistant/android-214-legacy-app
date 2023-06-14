package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.databinding.Ics214FragmentDetailsEditorBinding
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.util.RequestCodes
import com.randomvoids.emassistant.view.ui.dialogs.DatePickerFragment
import com.randomvoids.emassistant.view.ui.dialogs.TimePickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class ICS214DetailsEditorFragment : Fragment() {
    @VisibleForTesting val ics214EditorViewModel: ICS214EditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = Ics214FragmentDetailsEditorBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ICS214DetailsEditorFragment
            ics214OperationPeriodStartDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment(ics214EditorViewModel.ics214LiveData.value!!.ics214Details.operationalPeriodStartTime)
                datePickerFragment.setTargetFragment(
                    this@ICS214DetailsEditorFragment,
                    RequestCodes.ICS214_OPERATIONAL_PERIOD_START_DATE_PICKER
                )
                datePickerFragment.show(parentFragmentManager, "OperationPeriodStartDatePicker")
            }
            ics214OperationPeriodStartTime.setOnClickListener {
                val datePickerFragment = TimePickerDialogFragment(ics214EditorViewModel.ics214LiveData.value!!.ics214Details.operationalPeriodStartTime)
                datePickerFragment.setTargetFragment(
                    this@ICS214DetailsEditorFragment,
                    RequestCodes.ICS214_OPERATIONAL_PERIOD_START_TIME_PICKER
                )
                datePickerFragment.show(parentFragmentManager, "OperationPeriodStartTimePicker")
            }
            ics214OperationPeriodEndDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment(ics214EditorViewModel.ics214LiveData.value!!.ics214Details.operationalPeriodEndTime)
                datePickerFragment.setTargetFragment(
                    this@ICS214DetailsEditorFragment,
                    RequestCodes.ICS214_OPERATIONAL_PERIOD_END_DATE_PICKER
                )
                datePickerFragment.show(parentFragmentManager, "OperationPeriodEndDatePicker")
            }
            ics214OperationPeriodEndTime.setOnClickListener {
                val datePickerFragment = TimePickerDialogFragment(ics214EditorViewModel.ics214LiveData.value!!.ics214Details.operationalPeriodEndTime)
                datePickerFragment.setTargetFragment(
                    this@ICS214DetailsEditorFragment,
                    RequestCodes.ICS214_OPERATIONAL_PERIOD_END_TIME_PICKER
                )
                datePickerFragment.show(parentFragmentManager, "OperationPeriodEndTimePicker")
            }
            viewModel = ics214EditorViewModel
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_with_save, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.save_button -> {
                ics214EditorViewModel.onClickSave()
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //TODO refactor all of this over to the viewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.ICS214_OPERATIONAL_PERIOD_START_DATE_PICKER -> {
                val ics214Details = ics214EditorViewModel.ics214LiveData.value!!.ics214Details
                val timeDiff = ics214Details.operationalPeriodEndTime.time - ics214Details.operationalPeriodStartTime.time
                ics214Details.operationalPeriodStartTime = data!!.getSerializableExtra("selectedDate") as Date
                val newEndTimeLong = ics214Details.operationalPeriodStartTime.time + timeDiff
                ics214Details.operationalPeriodEndTime = Date(newEndTimeLong)
                ics214EditorViewModel.ics214LiveData.postValue(updateActivityModelTimes(ics214EditorViewModel.ics214LiveData.value!!))
            }
            RequestCodes.ICS214_OPERATIONAL_PERIOD_START_TIME_PICKER -> {
                val ics214Details = ics214EditorViewModel.ics214LiveData.value!!.ics214Details
                val timeDiff = ics214Details.operationalPeriodEndTime.time - ics214Details.operationalPeriodStartTime.time
                ics214Details.operationalPeriodStartTime = data!!.getSerializableExtra("selectedTime") as Date
                val newEndTimeLong = ics214Details.operationalPeriodStartTime.time + timeDiff
                ics214Details.operationalPeriodEndTime = Date(newEndTimeLong)
                ics214EditorViewModel.ics214LiveData.postValue(updateActivityModelTimes(ics214EditorViewModel.ics214LiveData.value!!))

            }
            RequestCodes.ICS214_OPERATIONAL_PERIOD_END_DATE_PICKER -> {
                val ics214Details = ics214EditorViewModel.ics214LiveData.value!!.ics214Details
                val newTime = data!!.getSerializableExtra("selectedDate") as Date
                if(newTime.before(ics214Details.operationalPeriodStartTime)) {
                    Toasty.error(requireContext(), "Operational Period can't end before it starts", Toast.LENGTH_LONG).show()
                } else {
                    ics214Details.operationalPeriodEndTime = newTime
                    ics214EditorViewModel.ics214LiveData.postValue(updateActivityModelTimes(ics214EditorViewModel.ics214LiveData.value!!))
                }
            }
            RequestCodes.ICS214_OPERATIONAL_PERIOD_END_TIME_PICKER -> {
                val ics214Details = ics214EditorViewModel.ics214LiveData.value!!.ics214Details
                val newTime = data!!.getSerializableExtra("selectedTime") as Date
                if(newTime.before(ics214Details.operationalPeriodStartTime)) {
                    Toasty.error(requireContext(), "Operational Period can't end before it starts", Toast.LENGTH_LONG).show()
                } else {
                    ics214Details.operationalPeriodEndTime = newTime
                    ics214EditorViewModel.ics214LiveData.postValue(updateActivityModelTimes(ics214EditorViewModel.ics214LiveData.value!!))
                }
            }
            else -> {
            }
        }
    }
    //TODO move this into the viewModel
    private fun updateActivityModelTimes(ics214: ICS214WithActivityLogAndResources): ICS214WithActivityLogAndResources {
        val startTime = Calendar.getInstance()
        startTime.time = ics214.ics214Details.operationalPeriodStartTime
        val endTime = Calendar.getInstance()
        endTime.time = ics214.ics214Details.operationalPeriodEndTime
        val activityLogEntries = ics214.activityLog
        val currentTime = Calendar.getInstance()
        activityLogEntries.forEach {
            currentTime.time = it.time
            currentTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR))
            currentTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH))
            currentTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH))
            if(currentTime.get(Calendar.HOUR_OF_DAY) < startTime.get(Calendar.HOUR_OF_DAY) ||
                currentTime.get(Calendar.HOUR_OF_DAY) == startTime.get(Calendar.HOUR_OF_DAY) && currentTime.get(Calendar.MINUTE) < startTime.get(Calendar.MINUTE)) {
                val newTime = Calendar.getInstance()
                newTime.time = endTime.time
                newTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                newTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
                it.time = newTime.time
            } else if (currentTime.get(Calendar.HOUR_OF_DAY) > endTime.get(Calendar.HOUR) ||
                currentTime.get(Calendar.HOUR_OF_DAY) == endTime.get(Calendar.HOUR) && currentTime.get(Calendar.MINUTE) > endTime.get(Calendar.MINUTE)) {
                val newTime = Calendar.getInstance()
                newTime.time = startTime.time
                newTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                newTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
                it.time = newTime.time
            }else {
                it.time = currentTime.time
            }
        }

        return ics214
    }
}