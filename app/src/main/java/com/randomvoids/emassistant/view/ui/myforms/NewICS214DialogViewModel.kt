package com.randomvoids.emassistant.view.ui.myforms

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.model.ICS214Details
import com.randomvoids.emassistant.model.Incident
import com.randomvoids.emassistant.model.IncidentId
import com.randomvoids.emassistant.repository.ICS214Repository
import com.randomvoids.emassistant.repository.IncidentRepository
import com.randomvoids.emassistant.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewICS214DialogViewModel @Inject constructor(
    private val ics214Repository: ICS214Repository,
    private val incidentRepository: IncidentRepository,
    private val userProfileRepository: UserProfileRepository
) : LiveCoroutinesViewModel() {
    //private var incidentFetchingIdLiveData: MutableLiveData<Int> = MutableLiveData()
    val incidentliveData: MutableLiveData<Incident?> =  MutableLiveData(Incident(incidentName = ""))

    /*
    init {
        incidentliveData = incidentFetchingIdLiveData.switchMap {
            launchOnViewModelScope {
                if (it < 1)
                    MutableLiveData(Incident(incidentName = ""))
                else
                    incidentRepository.getIncidentById(it).asLiveData()
            }
        } as MutableLiveData<Incident?>
    }*/

    fun saveNewIncidentDialogData(context: Context) {
        incidentliveData.value?.let {
            if(!it.incidentName.isNullOrBlank()) {
                GlobalScope.launch {
                val user = userProfileRepository.getUserById(1)!!
                val id = incidentRepository.createIncident(it)
                val ics214 = ICS214Details(
                    id=0,
                    incidentId = id.toInt(),
                    operationalPeriodStartTime = it.incidentStartDateTime,
                    operationalPeriodEndTime = Date(it.incidentStartDateTime.time + 24*60*60*1000),
                    preparedByName = user.firstName + " " + user.lastName
                )
                val ics214Id = ics214Repository.saveNewICS214Details(ics214)
                }
            } else {
                Toasty.error(context, "Unable to create a new incident with an empty name.", Toast.LENGTH_LONG, true).show();
            }
        }
    }

    fun loadIncident(int: IncidentId) {
        //incidentFetchingIdLiveData.value = int
    }
}