package com.randomvoids.emassistant.util.icsforms.ics213

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.randomvoids.emassistant.model.IncidentId
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

typealias ICS213Id = Int
@Parcelize
@Entity(tableName = "ics_213_details")
data class ICS213Details (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ics213_id") val id: ICS213Id,
    @ColumnInfo(name = "ics213_uuid") val uuid: UUID? = null,
    @ColumnInfo(name = "incident_id") val incidentId: IncidentId?,
    @ColumnInfo(name = "message_to") var messageTo: String? = "",
    @ColumnInfo(name = "message_from") var messageFrom: String,
    @ColumnInfo(name = "subject") var subject: String? = "",
    @ColumnInfo(name = "timestamp_of_message") var messageTime: Date,
    @ColumnInfo(name = "message") var message: String? = "",
    @ColumnInfo(name = "approved_by") var approvedBy: String? = "",
    @ColumnInfo(name = "approved_by_position_title")  var approvedByPositionTitle: String? = "",
    //approved by signature?
    @ColumnInfo(name = "reply") var reply: String? = "",
    @ColumnInfo(name = "replied_by") var repliedBy: String? = "",
    @ColumnInfo(name = "replied_by_position_title") var repliedByPositionTitle: String? = "",
    @ColumnInfo(name = "reply_timestamp") var replyTimestamp: Date?,
    @ColumnInfo(name = "created_by_uuid") val createdByUuid: UUID? = null
) : Parcelable {
    fun getFormattedReplyTimestamp(): String {
        val sdf = SimpleDateFormat("MM/dd/YYYY HH:mm")
        return sdf.format(replyTimestamp)
    }

    fun getFormattedMessageDate(): String {
        val sdf = SimpleDateFormat("MM/dd/YY")
        return sdf.format(messageTime)
    }

    fun getFormattedMessageTime(): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(messageTime)
    }
}