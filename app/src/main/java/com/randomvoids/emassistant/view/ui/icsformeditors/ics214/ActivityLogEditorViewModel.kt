package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.ActivityLogEntry
import com.randomvoids.emassistant.model.ICS214Id
import com.randomvoids.emassistant.repository.ICS214Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ActivityLogEditorViewModel  @Inject constructor(
    private val ics214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    //for the list
    private var activityLogEntryListFetchingLiveData: MutableLiveData<Int> = MutableLiveData()
    private var activityLogEntryListCountLiveData: LiveData<Int?>
    val activityLogEntryListLiveData : LiveData<List<ActivityLogEntry>?>
    //for editing an individual entry
    private var activityLogEntryEditorFetchingLiveData: MutableLiveData<Int> = MutableLiveData()
    val activityLogEntryEditorLiveData: MutableLiveData<ActivityLogEntry?>

    init {
        Timber.d("initializing ActivityLogEditorViewModel")
        activityLogEntryListCountLiveData = activityLogEntryListFetchingLiveData.switchMap {
            launchOnViewModelScope {
                ics214Repository.getLiveActivityLogEntriesCount(it).asLiveData()
            }
        }
        activityLogEntryListLiveData = activityLogEntryListCountLiveData.switchMap {
            launchOnViewModelScope {
                ics214Repository.findActivityLogForICS214By214Id(activityLogEntryListFetchingLiveData.value!!).asLiveData()
            }
        }
        activityLogEntryEditorLiveData = activityLogEntryEditorFetchingLiveData.switchMap {
            launchOnViewModelScope {
                if(it < 1)
                    MutableLiveData(ActivityLogEntry(ics214Id = activityLogEntryListFetchingLiveData.value!!, time = Date(), activityDetails = ""))
                else
                    ics214Repository.getActivityLogEntryById(it).asLiveData()
            }
        } as MutableLiveData<ActivityLogEntry?>
    }

    fun updateLogEntryEditorDate(time: Date) {
        GlobalScope.launch {
            val ics214Details = ics214Repository.getICS214DetailsByICS214Id(activityLogEntryListFetchingLiveData.value!!)!!
            val startTime = Calendar.getInstance()
            startTime.time = ics214Details.operationalPeriodStartTime
            val endTime = Calendar.getInstance()
            endTime.time = ics214Details.operationalPeriodEndTime

            val currentTime = Calendar.getInstance()
            currentTime.time = time
            currentTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR))
            currentTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH))
            currentTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH))
            Timber.d("start: " + currentTime.get(Calendar.HOUR_OF_DAY) + " vs " + startTime.get(Calendar.HOUR_OF_DAY))
            if(currentTime.get(Calendar.HOUR_OF_DAY) < startTime.get(Calendar.HOUR_OF_DAY) ||
                currentTime.get(Calendar.HOUR_OF_DAY) == startTime.get(Calendar.HOUR_OF_DAY) && currentTime.get(Calendar.MINUTE) < startTime.get(Calendar.MINUTE)) {
                val newTime = Calendar.getInstance()
                newTime.time = endTime.time
                newTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                newTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
                activityLogEntryEditorLiveData.value!!.time = newTime.time
            } else if (currentTime.get(Calendar.HOUR_OF_DAY) > endTime.get(Calendar.HOUR) ||
                currentTime.get(Calendar.HOUR_OF_DAY) == endTime.get(Calendar.HOUR) && currentTime.get(Calendar.MINUTE) > endTime.get(Calendar.MINUTE)) {
                val newTime = Calendar.getInstance()
                newTime.time = startTime.time
                newTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
                newTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
                activityLogEntryEditorLiveData.value!!.time = newTime.time
            } else {
                activityLogEntryEditorLiveData.value!!.time = currentTime.time
            }
            Timber.d("adjusting time to " + activityLogEntryEditorLiveData.value!!.time + " vs " + currentTime.time)
            activityLogEntryEditorLiveData.postValue(activityLogEntryEditorLiveData.value)
        }
    }
    @MainThread
    fun onActivityLogEntryEditButtonClick(view: View, activityLogEntryId: Int) {
        val activityLogEntryDialogFragment = ActivityLogEntryDialogFragment()
        activityLogEntryDialogFragment.setTargetFragment(FragmentManager.findFragment(view), 9000)
        activityLogEntryDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "activity_log_entry_dialog")
        activityLogEntryEditorFetchingLiveData.value=activityLogEntryId
    }

    @MainThread
    fun onActivityLogEntryDeleteButtonClick(view: View, activityLogEntryId: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this activity log entry?")
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener() { _, _ ->
                GlobalScope.launch {
                    ics214Repository.deleteActivityLogEntryById(activityLogEntryId)
                }
                })
            .setNegativeButton(android.R.string.cancel, null).show()
        activityLogEntryEditorFetchingLiveData.value=activityLogEntryId
    }

    fun onActivityLogEntryNewButtonClick(view: View) {
        val activityLogEntryDialogFragment = ActivityLogEntryDialogFragment()
        activityLogEntryDialogFragment.setTargetFragment(FragmentManager.findFragment(view), 9001)
        activityLogEntryDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "activity_log_entry_dialog")
        activityLogEntryEditorFetchingLiveData.value=-1
    }

    fun saveActiveActivityLogEntryEdit() {
        GlobalScope.launch {
            activityLogEntryEditorLiveData.value?.let {
                ics214Repository.saveActivityLogEntry(it) }
            activityLogEntryEditorFetchingLiveData.postValue(activityLogEntryEditorFetchingLiveData.value)
        }
    }

    @MainThread
    fun fetchActivityLog(id: ICS214Id) {
        activityLogEntryListFetchingLiveData.value = id
    }

}