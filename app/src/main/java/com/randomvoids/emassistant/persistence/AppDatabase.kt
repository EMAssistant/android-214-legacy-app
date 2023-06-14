package com.randomvoids.emassistant.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.randomvoids.emassistant.model.*

@Database(entities = [User::class, Incident::class, ICS214Details::class, ICSResource::class, ActivityLogEntry::class, MileageLogEntry::class], version=2, exportSchema = true)
@TypeConverters(value = [TypeResponseConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun incidentDao(): IncidentDao
    abstract fun ics214Dao(): ICS214Dao
    abstract fun icsResourceDAO(): ICSResourceDao
    abstract fun activityLogEntryDao(): ActivityLogEntryDao
    abstract fun mileageLogDao(): MileageLogDao
}