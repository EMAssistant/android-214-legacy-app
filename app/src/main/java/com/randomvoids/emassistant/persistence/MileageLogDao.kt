package com.randomvoids.emassistant.persistence

import androidx.room.*
import com.randomvoids.emassistant.model.MileageLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MileageLogDao {
    @Query("SELECT * FROM mileage_log ORDER BY drive_date")
    suspend fun getAll(): List<MileageLogEntry>

    @Query("SELECT count(*) FROM mileage_log")
    suspend fun getCount(): Int

    @Query("SELECT count(*) FROM mileage_log")
    fun getLiveCount(): Flow<Int>

    @Query("SELECT * FROM mileage_log WHERE mileage_log_entry_id=:id")
    fun getMileageLogEntryById(id: Int): MileageLogEntry

    @Insert
    suspend fun saveMileageLogEntry(mileageLogEntry: MileageLogEntry): Long

    @Update
    suspend fun updateMileageLogEntry(mileageLogEntry: MileageLogEntry)

    @Delete
    suspend fun deleteMileageLogEntry(mileageLogEntry: MileageLogEntry)
}