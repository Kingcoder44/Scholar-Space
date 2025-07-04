package com.example.bookbeacon.ui.presentation.Subject

import android.view.View
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookbeacon.domain.model.Subject
import com.example.bookbeacon.domain.model.Task
import com.example.bookbeacon.domain.repository.SubjectRepository
import com.example.bookbeacon.ui.presentation.navArgs
import com.example.scholar_space.util.SnackbarEvent
import com.example.scholar_space.util.toHours
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectVM @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository : SessionRepository,
    private val taskRepository : TaskRepository,
    private val savedStateHandle: SavedStateHandle
) :ViewModel(){

    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()

    //to update
    private val _state =  MutableStateFlow(SubjectState())
    //to read
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(navArgs.subjectId)
    ){
            state, upcomingTasks, completedTask, recentSessions, totalSessionsDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionsDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackBarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetch_Subject()
    }
    fun onEvent(event : SubjectEvent){
        when(event)
        {
            SubjectEvent.DeleteSession -> TODO()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> TODO()
            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(
                        goalStudyHours = event.hours
                    )
                }
            }
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(
                        subjectCardColors = event.color
                    )
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(
                        subjectName = event.name
                    )
                }
            }
            is SubjectEvent.OnTaskIsCompleteChange -> {

                updateTask(event.task)
            }
            SubjectEvent.UpdateProgress -> {

                _state.update {
                    val goalStudyHours = state.value.goalStudyHours?.takeIf { it.isNotBlank() }?.toFloatOrNull() ?: 1f

                    it.copy(
                        progress = (state.value.studiedHours/ goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
            SubjectEvent.UpdateSubject -> updateSubject()
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackBar(message = "Subject updated successfully.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackBar(
                        message = "Couldn't update subject. ${e.message}",
                        duaration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetch_Subject(){
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let {
                subject ->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) },
                        currentSubjectId = subject.subjectId
                    )
                }
            }
        }
    }
    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackBar(message = "Subject deleted successfully")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackBar(message = "No Subject to delete")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackBar(
                        message = "Couldn't delete subject. ${e.message}",
                        duaration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                if(task.isComplete)
                {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackBar(message = "Saved in upcoming tasks."))

                }
                else{

                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackBar(message = "Saved in completed tasks."))
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackBar(
                        "Couldn't update task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }
}