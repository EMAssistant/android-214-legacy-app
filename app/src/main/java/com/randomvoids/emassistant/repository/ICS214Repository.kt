package com.randomvoids.emassistant.repository

import androidx.annotation.WorkerThread
import com.randomvoids.emassistant.model.*
import com.randomvoids.emassistant.persistence.ActivityLogEntryDao
import com.randomvoids.emassistant.persistence.ICS214Dao
import com.randomvoids.emassistant.persistence.ICSResourceDao
import com.randomvoids.emassistant.persistence.IncidentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class ICS214Repository @Inject constructor(
    private val ics214Dao: ICS214Dao,
    private val activityLogEntryDao: ActivityLogEntryDao,
    private val incidentDao: IncidentDao,
    private val icsResourceDao: ICSResourceDao
) {
    @WorkerThread
    suspend fun findActivityLogForICS214By214Id(id: ICS214Id) = flow {
        emit(activityLogEntryDao.getByICS214Id(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getAllFullICS214s() = flow {
        emit(ics214Dao.getAll())
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getFullICS214ByIdOnFlow(id: Int) = flow {
        emit(ics214Dao.findICS214ById(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getFullICS214ById(id: Int): ICS214WithActivityLogAndResources? {
        return ics214Dao.findICS214ById(id)
    }

    @WorkerThread
    fun deleteICS214(ics214: ICS214WithActivityLogAndResources) {
        icsResourceDao.deleteICSResources(ics214.resources)
        activityLogEntryDao.deleteActivityLogEntries(ics214.activityLog)
        ics214Dao.deleteICS214Details(ics214.ics214Details)
    }

    @WorkerThread
    suspend fun getICS214DetailsByICS214Id(ics214Id: ICS214Id)  = ics214Dao.findICS214DetailsById(ics214Id)

    @WorkerThread
    suspend fun getResourcesBy214Id(id: ICS214Id, onSuccess: (Int) -> Unit?) = flow {
        val res = icsResourceDao.getByICS214Id(id)
        emit(res)
        onSuccess(res.size)
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getResourceItemById(id: Int) = flow {
        emit(icsResourceDao.getById(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun deleteResourceItemById(resourceId: Int) {
        val item = icsResourceDao.getById(resourceId)
        icsResourceDao.deleteICSResource(item)
    }

    fun saveICSResource(icsResource: ICSResource) {
        Timber.d("trying to save")
        if(icsResource.resourceId > 0)
            icsResourceDao.updateICSResource(icsResource)
        else
            icsResourceDao.saveNewICSResource(icsResource)
    }

    @WorkerThread
    suspend fun getActivityLogEntryById(id: Int) = flow {
        emit(activityLogEntryDao.getActivityLogEntryById(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun deleteActivityLogEntryById(id: Int) {
        val log = activityLogEntryDao.getActivityLogEntryById(id)
        return activityLogEntryDao.deleteActivityLogEntry(log)
    }

    @WorkerThread
    fun getLiveICS214Count() = ics214Dao.getLiveCount().flowOn(Dispatchers.IO)

    @WorkerThread
    fun getLiveActivityLogEntriesCount(ics214Id: ICS214Id) = activityLogEntryDao.getLiveActivityLogEntryCount(ics214Id).flowOn(Dispatchers.IO)

    @WorkerThread
    fun saveNewICS214Details(ics214Details: ICS214Details) = ics214Dao.saveICS214Details(ics214Details).toInt()

    @WorkerThread
    fun updateICS214Details(icS214Details: ICS214Details) = ics214Dao.updateICS214Details(icS214Details)

    @WorkerThread
    fun updateActivityLogEntries(activityLogEntries: List<ActivityLogEntry>) = activityLogEntryDao.updateActivityLogEntries(activityLogEntries)

    fun saveActivityLogEntry(activityLogEntry: ActivityLogEntry) {
        if (activityLogEntry.activityLogEntryId > 0)
            activityLogEntryDao.updateActivityLogEntry(activityLogEntry)
        else
            activityLogEntryDao.saveNewActivityLogEntry(activityLogEntry)
    }

    private fun saveFullICS214(ics214: ICS214WithActivityLogAndResources) {
        incidentDao.createNewIncident(ics214.incident)
        ics214Dao.saveICS214Details(ics214.ics214Details)
        activityLogEntryDao.insertActivityLog(ics214.activityLog)
        icsResourceDao.insertICSResource(*ics214.resources.toTypedArray())
    }

    @WorkerThread
    fun seed() {
        if(ics214Dao.getCount()==0) {
            val ics214 = ICS214WithActivityLogAndResources(
                ics214Details=ICS214Details(
                    id=1,
                    incidentId = 1,
                    teamName = "FAST 1",
                    teamIcsPosition = "FAST Task Force",
                    homeAgency = "Roughnecks",
                    operationalPeriodStartTime = Date(Date().time-12*60*60*1000),
                    operationalPeriodEndTime = Date(Date().time+12*60*60*1000),
                    preparedByDateTime = Date(),
                    preparedByName = "Carl Jenkins",
                    preparedByPositionTitle = "Scribe"
                ),
                activityLog = listOf(
                    ActivityLogEntry(activityLogEntryId = 1, ics214Id = 1, time =  Date(Date().time-10*60*1000), activityDetails = "splashed around in the water"),
                    ActivityLogEntry(activityLogEntryId = 2, ics214Id = 1, time = Date(Date().time-2*60*1000), activityDetails = "went back inside to watch the storm")
                ),
                resources = listOf(
                    ICSResource(resourceId = 1, ics214Id = 1, name = "John Rico", icsPosition = "Team Lead", homeAgency = "Roughnecks"),
                    ICSResource(resourceId = 2, ics214Id = 1, name = "Carl Jenkins", icsPosition = "Scribe", homeAgency = "Roughnecks"),
                    ICSResource(resourceId = 3, ics214Id = 1, name = "Carmen Ibanez", icsPosition = "Driver", homeAgency = "Roughnecks")
                ),
                incident=Incident(id=1,incidentName="A big flooding",incidentStartDateTime =  Date(Date().time-12*60*60*1000))
            )
            try {
                saveFullICS214(ics214)
            } catch (e: Exception) {
                Timber.w("trying to seed something new a second time and it says NO!")
            }
        }
    }
}