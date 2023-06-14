package com.randomvoids.emassistant.persistence

import androidx.room.*
import com.randomvoids.emassistant.model.Incident
import com.randomvoids.emassistant.model.IncidentId

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents")
    fun getAllIncidents(): List<Incident>

    @Query("SELECT * FROM incidents WHERE incident_id = :id")
    fun getIncidentById(id: IncidentId): IncidentId

    @Insert
    fun createNewIncident(incident: Incident): Long

    @Update
    fun updateIncident(incident: Incident)

    @Delete
    fun deleteIncident(incident: Incident)
}