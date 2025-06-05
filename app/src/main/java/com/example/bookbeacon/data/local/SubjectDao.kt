package com.example.bookbeacon.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.bookbeacon.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao
{
    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query("select count(*) from Subject_Names")
    fun getTotalSubjectCount() : Flow<Int>

    @Query("select sum(goalHours) from subject_names")
     fun getTotalGoalHours() : Flow<Float>

    @Query("select * from subject_names where subjectId = :subjectId")
    suspend fun getSubjectById(subjectId : Int) : Subject?


    @Query("delete from subject_names where subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)

    @Query("select * from Subject_Names")
    fun getAllSubjects(): Flow<List<Subject>>
}