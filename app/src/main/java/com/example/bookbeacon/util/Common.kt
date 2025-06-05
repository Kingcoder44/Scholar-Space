package com.example.scholar_space.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import com.example.bookbeacon.ui.presentation.theme.Green
import com.example.bookbeacon.ui.presentation.theme.Orange
import com.example.bookbeacon.ui.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title : String, val color : Color,val value:Int)
{
    LOW("Low",color= Green,0),
    MEDIUM("Medium",color= Orange,1),
    HIGH("High",color= Red,2);

    companion object{

        fun fromInt(value : Int) = entries.firstOrNull{it.value == value} ?:MEDIUM
    }
}

fun Long?.changeMillisToDateString() : String{
    val date : LocalDate = this?.let{timestamp->
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Long.toHours(): Float{
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}

sealed class SnackbarEvent{

    data class ShowSnackBar(
        val message : String,
        val duaration : SnackbarDuration = SnackbarDuration.Short
    ) :SnackbarEvent()
    data object NavigateUp : SnackbarEvent()
}

fun Int.pad() : String{
    return this.toString().padStart(length = 2, padChar = '0')
}
