package com.example.bookbeacon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bookbeacon.domain.model.Session
import com.example.bookbeacon.domain.model.Subject
import com.example.bookbeacon.domain.model.Task
import com.example.studysmart.data.local.ColorListConvertor
import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.data.local.TaskDao

@Database(
    entities = [Subject::class,Session::class,Task::class],
    version = 1
)
@TypeConverters(ColorListConvertor::class)
abstract class AppDB : RoomDatabase() {
    abstract fun subjectDao() : SubjectDao

    abstract fun taskDao() : TaskDao

    abstract fun sessionDao() : SessionDao
}