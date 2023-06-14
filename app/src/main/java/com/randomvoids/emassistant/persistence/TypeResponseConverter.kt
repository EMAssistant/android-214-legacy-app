package com.randomvoids.emassistant.persistence

import androidx.room.TypeConverter
import java.time.Instant
import java.util.*

object TypeResponseConverter {

    @JvmStatic
    @TypeConverter
    fun fromStringToUuid(value: String?): UUID? = value?.let { UUID.fromString(it) }

    @JvmStatic
    @TypeConverter
    fun fromUUIDtoString(uuid: UUID?): String? = uuid?.let { it.toString() }

    @JvmStatic
    @TypeConverter
    fun fromDatetoLong(date: Date?): Long? = date?.let { it.toInstant().toEpochMilli() }

    @JvmStatic
    @TypeConverter
    fun fromLongtoDate(long: Long?) : Date? = long?.let { Date.from(Instant.ofEpochMilli(it)) }
}