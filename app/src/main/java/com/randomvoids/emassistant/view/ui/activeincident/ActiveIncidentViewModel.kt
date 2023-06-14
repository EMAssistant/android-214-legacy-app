package com.randomvoids.emassistant.view.ui.activeincident

import com.randomvoids.emassistant.base.LiveCoroutinesViewModel
import com.randomvoids.emassistant.repository.ICS214Repository
import com.randomvoids.emassistant.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActiveIncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val ics214Repository: ICS214Repository
) : LiveCoroutinesViewModel() {
    fun setGPSTrackingStatus(enabled: Boolean) {

    }
}