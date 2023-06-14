package com.randomvoids.emassistant.view.ui.icsformeditors.ics214

import android.content.Context
import android.net.Uri
import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.randomvoids.emassistant.R
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.repository.ICS214Repository
import com.randomvoids.emassistant.repository.IncidentRepository
import com.randomvoids.emassistant.util.icsforms.ics214.ICS214Saver
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ICS214EditorViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val ics214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    //specific to ics214 base details view
    private var ics214FetchIdMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    val ics214LiveData: MutableLiveData<ICS214WithActivityLogAndResources?>
    val isSavingPDF: ObservableBoolean = ObservableBoolean(false)

    init {
        Timber.d("Initializing ICS214EditorViewModel")
        ics214LiveData = ics214FetchIdMutableLiveData.switchMap {
            launchOnViewModelScope {
                ics214Repository.getFullICS214ByIdOnFlow(it).asLiveData()
            }
        } as MutableLiveData<ICS214WithActivityLogAndResources?>
    }

    fun onClickSave() {
        GlobalScope.launch {
            val existingIncident = ics214Repository.getFullICS214ById(ics214FetchIdMutableLiveData.value!!)!!
            if(existingIncident.incident.incidentName != ics214LiveData.value!!.incident.incidentName)
                incidentRepository.updateIncident(ics214LiveData.value!!.incident)
            ics214Repository.updateICS214Details(ics214LiveData.value!!.ics214Details)
            ics214Repository.updateActivityLogEntries(ics214LiveData.value!!.activityLog)
            ics214FetchIdMutableLiveData.postValue(ics214FetchIdMutableLiveData.value!!)
        }
    }
    fun exportICS214ToPDF(context: Context, directoryUri: Uri) {
        val pickedDir: DocumentFile = DocumentFile.fromTreeUri(context, directoryUri)!!
        isSavingPDF.set(true)
        GlobalScope.launch {
            val ics214 = ics214Repository.getFullICS214ById(ics214FetchIdMutableLiveData.value!!)!!
            ics214.ics214Details.preparedByDateTime = Date()
            ics214Repository.updateICS214Details(ics214.ics214Details)
            PDFBoxResourceLoader.init(context)
            val newFile = pickedDir.createFile("application/pdf", "ICS 214 - " + ics214.incident.incidentName + " " +ics214.ics214Details.getFormattedOperationalPeriodStartDate() + ".pdf")
            val out: OutputStream = context.contentResolver.openOutputStream(newFile!!.uri)!!
            val ics214InputStream = context.resources.openRawResource(R.raw.ics214)
            val signatureFile = File(context.filesDir, "humanSignature.png")
            ICS214Saver.saveICS214ToPDF(
                onSuccess = { isSavingPDF.set(false) },
                onError = { Timber.d("error saving pdf: %s", it); isSavingPDF.set(false) },
                ics214LiveData.value!!.incident, ics214, signatureFile, ics214InputStream, out)
        }
    }

    fun deleteCurrentICS214() {
        GlobalScope.launch {
            ics214Repository.deleteICS214(ics214LiveData.value!!)
        }
    }
    @MainThread
    fun fetchFullICS214(id: Int) {
        ics214FetchIdMutableLiveData.value = id
    }
}