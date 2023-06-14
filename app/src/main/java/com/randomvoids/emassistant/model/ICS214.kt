package com.randomvoids.emassistant.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

typealias ICS214Id = Int
@Parcelize
@Entity(tableName = "ics_214_details")
data class ICS214Details (
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "ics214_id") val id: ICS214Id,
    @ColumnInfo(name = "ics214_uuid") val uuid: UUID? = null,
    @ColumnInfo(name = "incident_id") val incidentId: IncidentId,
    @ColumnInfo(name = "operation_period_start_time") var operationalPeriodStartTime: Date,
    @ColumnInfo(name = "operation_period_end_time") var operationalPeriodEndTime: Date,
    @ColumnInfo(name = "team_name") var teamName: String = "",
    @ColumnInfo(name = "team_ics_position") var teamIcsPosition: String = "",
    @ColumnInfo(name = "home_agency") var homeAgency: String? = "",
    @ColumnInfo(name = "prepared_by_name") var preparedByName: String,
    @ColumnInfo(name = "prepared_by_position_title") var preparedByPositionTitle: String = "Scribe",
    @ColumnInfo(name = "prepared_by_date_time") var preparedByDateTime: Date? = null, //TODO on first export, if this is null, set it?
    @ColumnInfo(name = "is_submitted") var isSubmitted: Boolean = false,
    @ColumnInfo(name = "created_by_uuid") val createdByUuid: UUID? = null
) : Parcelable {
    fun getFormattedPreparedByDateTime(): String {
        val sdf = SimpleDateFormat("MM/dd/YYYY HH:mm")
        return sdf.format(preparedByDateTime!!)
    }

    fun getFormattedOperationalPeriodStartDate(): String {
        val sdf = SimpleDateFormat("MM/dd/YY")
        return sdf.format(operationalPeriodStartTime)
    }

    fun getFormattedOperationalPeriodEndDate(): String {
        val sdf = SimpleDateFormat("MM/dd/YY")
        return sdf.format(operationalPeriodEndTime)
    }

    fun getFormattedOperationalPeriodStartTime(): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(operationalPeriodStartTime)
    }

    fun getFormattedOperationalPeriodEndTime(): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(operationalPeriodEndTime)
    }
}

@Parcelize
@Entity (tableName = "ics_resources")
data class ICSResource (
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "resource_id") val resourceId: Int = 0,
    @ColumnInfo(name = "resource_uuid") val resourceUuid: UUID? = null,
    @ColumnInfo(name = "ics214_id") val ics214Id: Int,
    @ColumnInfo(name = "resource_name") var name: String,
    @ColumnInfo(name = "ics_position") var icsPosition: String,
    @ColumnInfo(name = "home_agency") var homeAgency: String,
    @ColumnInfo(name = "resource_link_uuid") var resourceLinkedUuid: UUID? = null,
    @ColumnInfo(name = "created_by_uuid") val createdByUuid: UUID? = null
) : Parcelable

object ICSResourceDiff : DiffUtil.ItemCallback<ICSResource>() {
    override fun areItemsTheSame(oldItem: ICSResource, newItem: ICSResource) = oldItem.resourceId == newItem.resourceId
    override fun areContentsTheSame(oldItem: ICSResource, newItem: ICSResource) = oldItem == newItem
}

@Parcelize
@Entity(tableName = "ics214_activity_log_entries")
data class ActivityLogEntry (
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "activity_log_entry_id") val activityLogEntryId: Int = 0,
    @ColumnInfo(name = "activity_log_entry_uuid") val activityLogEntryUuid: UUID? = null,
    @ColumnInfo(name = "ics214_id") val ics214Id: Int,
    @ColumnInfo(name = "activity_time") var time: Date,
    @ColumnInfo(name = "activity_details") var activityDetails: String,
    @ColumnInfo(name = "created_by_uuid") val createdByUuid: UUID? = null
) : Parcelable {
    fun getFormattedEntryTime(): String {
        val sdf = SimpleDateFormat("HH:mm")
        val cal = Calendar.getInstance()
        cal.time = time
        return sdf.format(time)
    }
}

object ActivityLogEntryDiff : DiffUtil.ItemCallback<ActivityLogEntry>() {
    override fun areItemsTheSame(oldItem: ActivityLogEntry, newItem: ActivityLogEntry) = oldItem.activityLogEntryId == newItem.activityLogEntryId
    override fun areContentsTheSame(oldItem: ActivityLogEntry, newItem: ActivityLogEntry) = oldItem == newItem
}

@Parcelize
data class ICS214WithActivityLogAndResources(
    @Embedded val ics214Details: ICS214Details,
    @Relation(parentColumn = "ics214_id", entityColumn = "ics214_id")
    val activityLog: List<ActivityLogEntry>,
    @Relation(parentColumn = "ics214_id", entityColumn = "ics214_id")
    val resources: List<ICSResource>,
    @Relation(parentColumn = "incident_id", entityColumn = "incident_id")
    val incident: Incident
) : Parcelable {
    fun getSortedActivityLog(): List<ActivityLogEntry> = activityLog.sortedBy { it.time }
}

object ICS214Diff : DiffUtil.ItemCallback<ICS214WithActivityLogAndResources>() {
    override fun areItemsTheSame(oldItem: ICS214WithActivityLogAndResources, newItem: ICS214WithActivityLogAndResources) = oldItem.ics214Details.id == newItem.ics214Details.id
    override fun areContentsTheSame(oldItem: ICS214WithActivityLogAndResources, newItem: ICS214WithActivityLogAndResources) = oldItem == newItem
}