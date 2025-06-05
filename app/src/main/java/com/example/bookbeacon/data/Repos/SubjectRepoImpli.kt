package com.example.bookbeacon.data.Repos

import com.example.bookbeacon.data.local.SubjectDao
import com.example.bookbeacon.domain.model.Subject
import com.example.bookbeacon.domain.repository.SubjectRepository
import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.data.local.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepoImpli @Inject constructor(
    private val subjectDao : SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository{
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
       return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTasksBySubjectId(subjectId)
        sessionDao.deleteSessionsBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
       return subjectDao.getAllSubjects()
    }
}