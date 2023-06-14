package com.randomvoids.emassistant.view.ui.profile.mileagelog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.MileageLogEntry
import com.randomvoids.emassistant.repository.MileageLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MileageLogViewModel @Inject constructor(
    private val mileageLogRepository: MileageLogRepository
) : LiveCoroutinesViewModel() {
    //the list
    private var listFetchingUpdater: LiveData<Int> = mileageLogRepository.getLiveMileageLogEntriesCount().asLiveData()
    val mileageLogListData: LiveData<List<MileageLogEntry>?>
    //individual entry
    private var mileageLogEntryFetchingIdLiveData: MutableLiveData<Int> = MutableLiveData()
    val mileageLogEntryLiveData: MutableLiveData<MileageLogEntry?>
    //for showing the dialog box thing
    val isValid: ObservableBoolean = ObservableBoolean(false)
    val errorTextPurposeInput: ObservableField<String> = ObservableField()
    val errorTextMilesDrivenInput: ObservableField<String> = ObservableField()
    //to show spinner when exporting to csv
    val isExporting: ObservableBoolean = ObservableBoolean(false)


    init {
        mileageLogListData = listFetchingUpdater.switchMap {
            launchOnViewModelScope {
                this.mileageLogRepository.getFullMileageLog().asLiveData()
            }
        }
        mileageLogEntryLiveData = mileageLogEntryFetchingIdLiveData.switchMap {
            launchOnViewModelScope {
                if(mileageLogEntryFetchingIdLiveData.value!! < 1) {
                    Timber.d("creating new log")
                    MutableLiveData(MileageLogEntry(mileageLogEntryId = 0, driveDate = Date(), milesDriven = 0.0F, purpose = ""))
                }
                else {
                    Timber.d("getting existing mileage log")
                    mileageLogRepository.getMileageLogEntryByIdOnFlow(it).asLiveData()
                }
            }
        } as MutableLiveData<MileageLogEntry?>
        isValid.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                Timber.d("isValid new value: " + ((sender as ObservableBoolean).get()))
            }
        })
    }

    @MainThread
    fun onMileageLogEntryEditButtonClick(view: View, mileageLogEntryId: Int) {
        mileageLogEntryFetchingIdLiveData.value = mileageLogEntryId
        isValid.set(true)
        val newMileageLogEntryDialogFragment = MileageLogEntryDialogFragment()
        newMileageLogEntryDialogFragment.setTargetFragment((FragmentManager.findFragment(view)), 101)
        newMileageLogEntryDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "mileage_log_entry_editor_dialog")
    }

    @MainThread
    fun onMileageLogEntryDeleteButtonClick(view: View, mileageLogEntryId: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this mileage log entry?")
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener() {_, _ ->
                GlobalScope.launch {
                    mileageLogRepository.deleteMileageLogEntryById(mileageLogEntryId)
                }
            })
            .setNegativeButton(android.R.string.cancel, null).show()
    }

    fun onNewMileageLogEntryButtonClick(view: View) {
        mileageLogEntryFetchingIdLiveData.value = -1
        val newMileageLogEntryDialogFragment = MileageLogEntryDialogFragment()
        newMileageLogEntryDialogFragment.setTargetFragment((FragmentManager.findFragment(view)), 101)
        newMileageLogEntryDialogFragment.show((FragmentManager.findFragment(view) as Fragment).parentFragmentManager, "mileage_log_entry_editor_dialog")
    }

    fun onPurposeTextChanged(s: CharSequence) {
        if(s.toString().isNullOrBlank()) {
            isValid.set(false)
            errorTextPurposeInput.set("Purpose is required")
        } else {
            validateRequiredFields(s.toString(), mileageLogEntryLiveData.value!!.milesDriven)
            errorTextPurposeInput.set("")
        }
    }

    fun onMilesDrivenTextChanged(s: CharSequence) {
        val temp = s.toString().toFloatOrNull()
        if(temp != null && temp > 0.1) {
            validateRequiredFields(mileageLogEntryLiveData.value!!.purpose, temp)
            errorTextMilesDrivenInput.set("")
        } else {
            errorTextMilesDrivenInput.set("Miles driven must be greater than 0.1")
            isValid.set(false)
        }
    }

    private fun validateRequiredFields(purpose: String, mileage: Float) {
        isValid.set(!purpose.isNullOrBlank() && mileage > 0.1)
    }

    fun saveActiveMileageLogEntry() {
        GlobalScope.launch {
            mileageLogEntryLiveData.value?.let {
                mileageLogRepository.saveMileageLogEntry(it)
            }
        }
    }

    fun updateMileageLogEntryEditorDate(date: Date) {
        mileageLogEntryLiveData.value!!.driveDate = date
        mileageLogEntryLiveData.postValue(mileageLogEntryLiveData.value)
    }

    fun exportToCSV(context: Context, directoryUri: Uri) {
        val pickedDir: DocumentFile = DocumentFile.fromTreeUri(context, directoryUri)!!
        isExporting.set(true)
        GlobalScope.launch {
            val sb = StringBuilder()
            sb.append("\"drive date\",\"purpose\",\"miles driven\",\"odometer start\",\"start location\",\"end location\"\n")
            mileageLogListData.value?.let { list ->
                if(!list.isNullOrEmpty()) {
                    list.forEach {
                        sb.append("\""+it.getFormattedDriveDate()+"\",")
                            .append("\"" + it.purpose + "\",")
                            .append("\"" + it.milesDriven + "\",")
                            .append("\"" + (if (it.odometerStart == 0) "" else it.odometerStart) + "\",")
                            .append("\"" + (if (it.startLocation.isNullOrBlank()) "" else it.startLocation) + "\",")
                            .append("\"" + (if (it.endLocation.isNullOrBlank()) "" else it.endLocation) + "\"\n")
                    }
                }
            }
            val newFile = pickedDir.createFile("text/csv","mileage_log.csv")
            withContext(Dispatchers.IO) {
                val out: OutputStream = context.contentResolver.openOutputStream(newFile!!.uri)!!
                val printer = PrintWriter(out)
                printer.write(sb.toString())
                printer.close()
                out.close()
            }
            isExporting.set(false)
            withContext(Dispatchers.Main) {
                Toasty.success(context, "Mileage log exported successfully", Toast.LENGTH_LONG, true).show();
            }
        }
    }
}