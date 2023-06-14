package com.randomvoids.emassistant.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

typealias IncidentId = Int
@Parcelize
@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "incident_id") val id:  IncidentId = 0,
    @ColumnInfo(name = "incident_uuid") val uuid: UUID? = null,
    @ColumnInfo(name = "incident_name") var incidentName: String,
    @ColumnInfo(name = "incident_number") var incidentNumber: String? = null,
    @ColumnInfo(name = "incident_start_date_time") var incidentStartDateTime: Date = Date(),
    @ColumnInfo(name = "incident_end_date_time")val endTime: Date? = Date(Date().time+24*60*60*1000),
    @ColumnInfo(name = "incident_summary") var incidentSummary: String? = null,
    @ColumnInfo(name = "location_description")var locationDescription: String? = null,
    @ColumnInfo(name = "created_by_uuid") val createdByUuid: UUID? = null
) : Parcelable {
    fun getIncidentListFormattedTime() : String {
        val sdf = SimpleDateFormat("MM/dd/yyyy")

        if (endTime != null) {
            val startCal = Calendar.getInstance()
            val endCal = Calendar.getInstance()
            startCal.time = incidentStartDateTime
            endCal.time = endTime
            if(startCal.get(Calendar.DAY_OF_YEAR) != endCal.get(Calendar.DAY_OF_YEAR))
                return sdf.format(incidentStartDateTime) + " - " + sdf.format(endTime)
        }
        return sdf.format(incidentStartDateTime)
    }
}

object IncidentDiff : DiffUtil.ItemCallback<Incident>() {
    override fun areItemsTheSame(oldItem: Incident, newItem: Incident) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Incident, newItem: Incident) = oldItem == newItem
}

val incidents = listOf(
    Incident(
        id=3,
        incidentName="Floods",
        incidentStartDateTime=Date(),
        endTime=Date(),
        incidentSummary = "everything is flooding!",
        locationDescription="Galveston"
    ),
    Incident(
        id=1,
        incidentName="Search",
        incidentStartDateTime=Date(),
        endTime=Date(Date().time+4*24*60*60*1000),
        locationDescription="LA"
    ),
    Incident(
        id=2,
        incidentName="Training",
        incidentStartDateTime=Date(),
        endTime=Date(),
        locationDescription="The Boondocks"
    ),
)