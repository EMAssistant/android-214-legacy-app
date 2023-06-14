package com.randomvoids.emassistant.repository

import androidx.annotation.WorkerThread
import com.randomvoids.emassistant.model.MileageLogEntry
import com.randomvoids.emassistant.persistence.MileageLogDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class MileageLogRepository @Inject constructor(
    private val mileageLogDao: MileageLogDao

) {
    @WorkerThread
    fun getLiveMileageLogEntriesCount() = mileageLogDao.getLiveCount().flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getFullMileageLog() = flow {
        emit(mileageLogDao.getAll())
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun getMileageLogEntryByIdOnFlow(id: Int) = flow {
        emit(mileageLogDao.getMileageLogEntryById(id))
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun getMileageLogEntryById(id: Int) = mileageLogDao.getMileageLogEntryById(id)

    @WorkerThread
    suspend fun saveMileageLogEntry(mileageLogEntry: MileageLogEntry) {
        mileageLogEntry.lastModifiedAt = Date()
        if (mileageLogEntry.mileageLogEntryId > 0)
            mileageLogDao.updateMileageLogEntry(mileageLogEntry)
        else
            mileageLogDao.saveMileageLogEntry(mileageLogEntry)
    }

    @WorkerThread
    suspend fun deleteMileageLogEntryById(mileageLogEntryId: Int) {
        val entry = mileageLogDao.getMileageLogEntryById(mileageLogEntryId)
        mileageLogDao.deleteMileageLogEntry(entry)
    }
}