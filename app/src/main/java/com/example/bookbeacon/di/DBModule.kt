package com.example.bookbeacon.di

import android.app.Application
import androidx.room.Room
import com.example.bookbeacon.data.local.AppDB
import com.example.bookbeacon.data.local.SubjectDao
import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //how dependecy should be provided
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun providedDB(
        application: Application
    ) : AppDB {
        return Room.databaseBuilder(application,
            AppDB::class.java,"ScholarSpace")
            .build()
    }

    @Provides
    @Singleton
    fun providesSubjectDAO(db : AppDB) : SubjectDao{
        return db.subjectDao()
    }
    @Provides
    @Singleton
    fun provideTaskDaoDao(database: AppDB): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDB): SessionDao {
        return database.sessionDao()
    }
}