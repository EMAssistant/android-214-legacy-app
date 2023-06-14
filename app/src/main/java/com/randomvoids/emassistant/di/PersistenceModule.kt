package com.randomvoids.emassistant.di

import android.app.Application
import androidx.room.Room
import com.randomvoids.emassistant.persistence.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "emassistant.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideICS214Dao(appDatabase: AppDatabase) = appDatabase.ics214Dao()

    @Provides
    @Singleton
    fun provideActivityLogEntryDao(appDatabase: AppDatabase) = appDatabase.activityLogEntryDao()

    @Provides
    @Singleton
    fun provideICSResourceDao(appDatabase: AppDatabase) = appDatabase.icsResourceDAO()

    @Provides
    @Singleton
    fun provideIncidentDao(appDatabase: AppDatabase) = appDatabase.incidentDao()

    @Provides
    @Singleton
    fun provideMileageLogDao(appDatabase: AppDatabase) = appDatabase.mileageLogDao()
}