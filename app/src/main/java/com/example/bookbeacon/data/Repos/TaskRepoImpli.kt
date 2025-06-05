package com.example.bookbeacon.data.Repos

import com.example.bookbeacon.domain.model.Task
import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepoImpli @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
       return taskDao.getTasksForSubject(subjectId).map {
               tasks->tasks.filter { it.isComplete.not() }
       }
           .map{
                   tasks->sortTask(tasks)
           }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getAllTasks().map {
                tasks->tasks.filter { it.isComplete }
        }
            .map{
                    tasks->sortTask(tasks)
            }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map {
            tasks->tasks.filter { it.isComplete.not() }
        }
            .map{
                tasks->sortTask(tasks)
            }
    }
    private fun sortTask(tasks : List<Task>) : List<Task>{
        return tasks.sortedWith(compareBy<Task>{it.dueDate}.thenByDescending { it.priority })
    }
}