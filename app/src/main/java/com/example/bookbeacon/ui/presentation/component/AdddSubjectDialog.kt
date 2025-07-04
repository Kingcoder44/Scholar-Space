package com.example.bookbeacon.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bookbeacon.domain.model.Subject

@Composable
fun AddSubjectDialog(
    isopen:Boolean,
    title:String = "Add/Update Subject",
    selectedColor : List<Color>,
    onColorChange : (List<Color>)->Unit,
    onDismissRequest : ()->Unit,
    onConfirmButton : ()->Unit,
    subjectName: String,
    goalHours : String,
    onSubjectNameChange : (String)->Unit,
    onGoalHourChange : (String) ->Unit

){
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null)}
    var goalHourError by rememberSaveable { mutableStateOf<String?>(null)}


    subjectNameError = when{
        subjectName.isBlank() -> "Please enter subject name"
        subjectName.length<2 -> "Subject name too short"
        subjectName.length>20 -> "Subject name too long"
        else->null
    }
    goalHourError = when{
        goalHours.isBlank() -> "Please enter goal study hours"
        goalHours.toFloatOrNull()==null -> "Invalid Number"
        goalHours.toFloat()<1f -> "Please set atleast 1 hour"
        goalHours.toFloat()>1000f -> "Please set atmost 1000 hour"
        else->null
    }
    if(isopen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {Text(text = title)},
            text = {
                Column {
                    Row (
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Subject.subjectCardColors.forEach{
                            colors ->
                            Box(
                                modifier = Modifier.size(24.dp)
                                    .clip(CircleShape)
                                    .border(width = 1.dp,
                                        color = if(colors == selectedColor)
                                        {
                                        Color.Black
                                    }else Color.Transparent,
                                        shape = CircleShape)
                                    .background(brush = Brush.verticalGradient(colors))
                                    .clickable{onColorChange(colors)}
                            )
                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name") },
                        singleLine = true,
                        isError = subjectNameError !=null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty()) }//what the error is
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = onGoalHourChange,
                        label = { Text(text = "Goal Study Hours") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = goalHourError !=null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHourError.orEmpty()) }//what the error is
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButton,
                    enabled = subjectNameError==null && goalHourError==null) {
                    Text("Save")
                }
            }

        )
    }

}