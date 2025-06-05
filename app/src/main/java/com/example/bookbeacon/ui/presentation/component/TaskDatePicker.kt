package com.example.bookbeacon.ui.presentation.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel",
    onDismissButtonClicked: () -> Unit,
    onConfirmButtonClicked: (Long?) -> Unit // Pass selected date
) {
    if (isOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissButtonClicked,
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = state.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val currentDate = LocalDate.now(ZoneId.systemDefault())
                            if (selectedDate >= currentDate) {
                                onConfirmButtonClicked(selectedDateMillis)
                            }
                        }
                    }
                ) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissButtonClicked) {
                    Text(text = dismissButtonText)
                }
            },
            content = {
                DatePicker(state = state) // Removed dateValidator
            }
        )
    }
}
