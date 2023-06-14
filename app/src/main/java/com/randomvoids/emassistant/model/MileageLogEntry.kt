package com.randomvoids.emassistant.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(tableName = "mileage_log")
data class MileageLogEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mileage_log_entry_id") val mileageLogEntryId: Int,
    @ColumnInfo(name = "server_side_id") val serverSideId: Int? = null,
    @ColumnInfo(name = "incident_id") var incidentId: Int? = null,
    @ColumnInfo(name = "drive_date") var driveDate: Date,
    @ColumnInfo(name = "odometer_start") var odometerStart: Int? = null,
    @ColumnInfo(name = "miles_driven") var milesDriven: Float,
    @ColumnInfo(name = "purpose") var purpose: String,
    @ColumnInfo(name = "start_location") var startLocation: String? = null,
    @ColumnInfo(name = "end_location") var endLocation: String? = null,
    @ColumnInfo(name = "last_modified_at") var lastModifiedAt: Date = Date()
) : Parcelable {
    fun getFormattedDriveDate(): String {
        val sdf = SimpleDateFormat("MM/dd/YYYY")
        return sdf.format(driveDate)
    }

    fun isStartLocationNotSet(): Boolean = startLocation.isNullOrBlank()

    fun isEndLocationNotSet(): Boolean = endLocation.isNullOrBlank()
}

object MileageLogEntryDiff : DiffUtil.ItemCallback<MileageLogEntry>() {
    override fun areItemsTheSame(oldItem: MileageLogEntry, newItem: MileageLogEntry) = oldItem.mileageLogEntryId == newItem.mileageLogEntryId
    override fun areContentsTheSame(oldItem: MileageLogEntry, newItem: MileageLogEntry) = oldItem == newItem
}