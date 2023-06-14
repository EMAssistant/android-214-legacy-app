package com.randomvoids.emassistant.persistence

import androidx.room.*
import com.randomvoids.emassistant.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT count(*) FROM User")
    suspend fun getCount(): Int

    @Query("SELECT * FROM User WHERE user_id = :id")
    suspend fun getUserProfile(id: Int): User?
}