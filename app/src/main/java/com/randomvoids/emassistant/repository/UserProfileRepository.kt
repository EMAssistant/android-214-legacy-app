package com.randomvoids.emassistant.repository

import androidx.annotation.WorkerThread
import com.randomvoids.emassistant.model.User
import com.randomvoids.emassistant.persistence.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userDao: UserDao
) {
    @WorkerThread
    suspend fun fetch(id: Int) = flow<User?> {
        val userProfile = userDao.getUserProfile(id)
        emit(userProfile)
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun insert(user: User): Long = userDao.insertUserProfile(user)

    @WorkerThread
    suspend fun update(user: User) = userDao.updateUser(user)

    suspend fun getUserById(id: Int) = userDao.getUserProfile(id)

    @WorkerThread
    suspend fun seed(): Boolean {
        if(userDao.getUserProfile(1) == null) {
            Timber.d("seeding the base user")
            userDao.insertUserProfile(User(userId=1, userUuid = UUID.randomUUID(), firstName = "New", lastName = "User"))
            return true
        }
        return false
    }
}