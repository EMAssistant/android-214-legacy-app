package com.randomvoids.emassistant.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class User (
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name="user_id") val userId: Int,
    @ColumnInfo(name="user_uuid") val userUuid: UUID? = null,
    @ColumnInfo(name="first_name") var firstName: String,
    @ColumnInfo(name="last_name") var lastName: String,
    @ColumnInfo(name="profile_image_file_location") val profileImageFileLocation: String? = null
) : Parcelable