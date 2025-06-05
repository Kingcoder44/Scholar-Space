package com.example.bookbeacon.ui.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.bookbeacon.domain.model.Session
import com.example.bookbeacon.domain.model.Task

sealed class DashboardEvent {
//ifinput coming from user then data class else object
    data object SaveSubject : DashboardEvent()

    data object DeleteSession : DashboardEvent()

    data class onDeleteSessionButonClick(val session : Session) : DashboardEvent()

    data class onTaskIsCompleteChange(val task : Task) : DashboardEvent()

    data class onSubjectCardColourChange(val color: List<Color>) : DashboardEvent()

    data class onSubjectNameChange(val name : String) : DashboardEvent()

    data class onGoalStudyHourChange(val hours : String) : DashboardEvent()
}