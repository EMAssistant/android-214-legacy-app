package com.randomvoids.emassistant.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "ics_201_details")
data class ICS201(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ics201_id") val ics201Id: Int,
    @ColumnInfo(name = "ics201_uuid") val ics201Uuid: UUID?,
    @ColumnInfo(name = "incident_id") val incidentId: IncidentId,
    @ColumnInfo(name = "situation_summary") var situationSummary: String? = "",
    @ColumnInfo(name = "prepared_by_user_id") var preparedByUserId: Int?,
    @ColumnInfo(name = "prepared_by_name") var preparedByName: String?, //preparedByUserId will override this if it exists, but that's not a guarantee
    @ColumnInfo(name = "prepared_by_position_title") var preparedByPositionTitle: String?,
    @ColumnInfo(name = "prepared_by_date_time") var preparedByDateTime: Date?,
) : Parcelable