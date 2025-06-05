package com.example.bookbeacon.ui.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    isopen:Boolean,
    title:String,
    bodyText : String,
    onDismissRequest : ()->Unit,
    onConfirmButton : ()->Unit
){

    if(isopen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = bodyText) },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButton) {
                    Text("Delete")
                }
            }

        )
    }

}