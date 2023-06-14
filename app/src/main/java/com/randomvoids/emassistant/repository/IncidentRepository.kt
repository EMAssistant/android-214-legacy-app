package com.randomvoids.emassistant.repository

import androidx.annotation.WorkerThread
import com.randomvoids.emassistant.model.Incident
import com.randomvoids.emassistant.model.IncidentId
import com.randomvoids.emassistant.persistence.IncidentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class IncidentRepository @Inject constructor(
    private val incidentDao: IncidentDao
) {
    @WorkerThread
    fun updateIncident(incident: Incident) = incidentDao.updateIncident(incident)

    @WorkerThread
    fun getIncidentById(id: IncidentId) = flow {
        emit(incidentDao.getIncidentById(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun createIncident(incident: Incident) = incidentDao.createNewIncident(incident)
}