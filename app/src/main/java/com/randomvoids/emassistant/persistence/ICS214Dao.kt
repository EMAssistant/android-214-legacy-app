package com.randomvoids.emassistant.persistence

import androidx.room.*
import com.randomvoids.emassistant.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ICS214Dao {

    @Transaction
    @Query("SELECT * FROM ics_214_details ORDER BY operation_period_start_time")
    fun getAll(): List<ICS214WithActivityLogAndResources>

    @Transaction
    @Query("SELECT * FROM ics_214_details WHERE ics214_id=:id")
    suspend fun findICS214ById(id: ICS214Id): ICS214WithActivityLogAndResources?

    @Transaction
    @Query("SELECT * FROM ics_214_details WHERE incident_id = :id")
    fun findICS214ByIncidentId(id: IncidentId): ICS214WithActivityLogAndResources?

    @Query("SELECT * FROM ics_214_details WHERE ics214_id=:id")
    suspend fun findICS214DetailsById(id: ICS214Id): ICS214Details?

    @Insert
    fun saveICS214Details(ics214Details: ICS214Details): Long

    @Update
    fun updateICS214Details(vararg ics214Details: ICS214Details)

    @Delete
    fun deleteICS214Details(ics214Details: ICS214Details)

    @Query("SELECT count(*) FROM ics_214_details")
    fun getCount(): Int

    @Query("SELECT count(*) FROM ics_214_details")
    fun getLiveCount(): Flow<Int>
}

@Dao
interface ICSResourceDao {
    @Query("SELECT * FROM ics_resources")
    fun getAll(): List<ICSResource>

    @Query("SELECT * FROM ics_resources WHERE ics214_id= :ics214Id")
    fun getByICS214Id(ics214Id: ICS214Id): List<ICSResource>

    @Query("SELECT * FROM ics_resources WHERE resource_id = :id")
    fun getById(id: Int): ICSResource

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertICSResource(vararg resources: ICSResource)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun saveNewICSResource(resource: ICSResource)

    @Update
    fun updateICSResource(resource: ICSResource)

    @Delete
    fun deleteICSResource(resource: ICSResource)

    @Delete
    fun deleteICSResources(resources: List<ICSResource>)
}

@Dao
interface ActivityLogEntryDao {
    @Query("SELECT * FROM ics214_activity_log_entries")
    fun getAll(): List<ActivityLogEntry>

    @Query("SELECT * FROM ics214_activity_log_entries WHERE ics214_id= :ics214Id ORDER BY activity_time")
    fun getByICS214Id(ics214Id: ICS214Id): List<ActivityLogEntry>

    @Query("SELECT * FROM ics214_activity_log_entries WHERE activity_log_entry_id= :id")
    fun getActivityLogEntryById(id: Int): ActivityLogEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLog(logEntries: List<ActivityLogEntry>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun saveNewActivityLogEntry(logEntry: ActivityLogEntry): Long

    @Update
    fun updateActivityLogEntry(logEntry: ActivityLogEntry)

    @Update
    fun updateActivityLogEntries(logEntries: List<ActivityLogEntry>)
    @Delete
    fun deleteActivityLogEntry(logEntry: ActivityLogEntry)

    @Query("SELECT count(*) FROM ics214_activity_log_entries WHERE ics214_id=:ics214Id")
    fun getLiveActivityLogEntryCount(ics214Id: ICS214Id): Flow<Int>

    @Delete
    fun deleteActivityLogEntries(logEntries: List<ActivityLogEntry>)
}