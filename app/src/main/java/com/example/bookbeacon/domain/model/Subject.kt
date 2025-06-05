package com.example.bookbeacon.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bookbeacon.ui.presentation.theme.gradient1
import com.example.bookbeacon.ui.presentation.theme.gradient2
import com.example.bookbeacon.ui.presentation.theme.gradient3
import com.example.bookbeacon.ui.presentation.theme.gradient4
import com.example.bookbeacon.ui.presentation.theme.gradient5

@Entity(tableName = "Subject_Names")
data class Subject(
    val name: String,
    val goalHours: Float, // Corrected from 'goalsHours' to 'goalHours' to match usage
    val colors: List<Int> ,// Corrected field name from 'color' to 'colors' to match usage
    @PrimaryKey(autoGenerate = true)
    val subjectId :Int? = null
) {
    companion object {
        val subjectCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
