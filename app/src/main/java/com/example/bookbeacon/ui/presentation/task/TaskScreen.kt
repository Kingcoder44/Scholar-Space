package com.example.bookbeacon.ui.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookbeacon.domain.model.Task
import com.example.bookbeacon.ui.presentation.Subject.SubjectVM
import com.example.bookbeacon.ui.presentation.component.DeleteDialog
import com.example.bookbeacon.ui.presentation.component.SubjectListBottomSheet
import com.example.bookbeacon.ui.presentation.component.TaskCheckBox
import com.example.bookbeacon.ui.presentation.component.TaskDatePicker
import com.example.bookbeacon.ui.presentation.theme.Red
import com.example.scholar_space.util.Priority
import com.example.scholar_space.util.SnackbarEvent
import com.example.scholar_space.util.changeMillisToDateString


import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val TaskId : Int?,
    val subjectId: Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
){

    val viewModel : TaskVM = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        onBackButtonClick = { navigator.navigateUp() },
        state = state,
        snackbarEvent = viewModel.snackBarEventFlow,
        onEvent = viewModel::onEvent
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state : TaskState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (TaskEvent)->Unit,
    onBackButtonClick: () -> Unit
)
{
    var deleteDialog by rememberSaveable {mutableStateOf(false)}

    var isdatePickerDialogOpen by rememberSaveable {mutableStateOf(false)}

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli() //to selecet current date
    )

    var isBottomSheetOpen by rememberSaveable {mutableStateOf(false)}
    val bottomSheetState = rememberModalBottomSheetState()

    val scope  = rememberCoroutineScope()


    var taskTitleError by rememberSaveable { mutableStateOf<String?>(null) }

    taskTitleError = when{
        state.title.isBlank() ->"Please enter task title"
        state.title.length<4 -> "Task title too short"
        state.title.length>30 -> "Task title too long"
        else->null
    }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest {
                event ->
            when(event){
                is SnackbarEvent.ShowSnackBar->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duaration
                    )
                }

                SnackbarEvent.NavigateUp -> {onBackButtonClick()}
            }
        }
    }

    DeleteDialog(
        isopen = deleteDialog,
        title = "Delete Task?",
        bodyText = "Are you sure you want to delete this task?",
        onDismissRequest = {deleteDialog = false },
        onConfirmButton = {
            onEvent(TaskEvent.DeleteTask)
            deleteDialog = false}

    )
    TaskDatePicker(
        state = datePickerState,
        isOpen = isdatePickerDialogOpen,
        onConfirmButtonClicked = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            isdatePickerDialogOpen = false},
        onDismissButtonClicked = {isdatePickerDialogOpen  =false}
    )
    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = {
            subject->
            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                if(!bottomSheetState.isVisible)
                    isBottomSheetOpen =false
            }
            onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
        },
        onDismissRequest = {isBottomSheetOpen = false}
    )
    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            taskScreenTopBar(
                isTaskExist = state.currentTaskId!=null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color,
                onBackButtonClick = onBackButtonClick,
                onCheckBoxClick = {onEvent(TaskEvent.OnIsCompleteChange)},
                onDeleteButtonClick = {deleteDialog=true}
            )
        }
    ){
        paddingValue->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValue)
                .padding(horizontal = 12.dp)
        ){
            OutlinedTextField(
                value = state.title,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {onEvent(TaskEvent.OnTitleChange(it))},
                label ={ Text(text = "Title") },
                singleLine = true,
                isError = taskTitleError!=null && state.title.isNotBlank(),
                supportingText = {
                    Text(text = taskTitleError.orEmpty())
                }
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.description,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {onEvent(TaskEvent.OnDescriptionChange(it))},
                label ={ Text(text = "Description") },

            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Due Date"
                ,style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isdatePickerDialogOpen = true}){
                    Icon(imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date")
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Priority"
                ,style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()){
                Priority.entries.forEach { priority ->

                        PriorityButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            label = priority.title,
                            bgColor = priority.color,
                            borderColor = if(priority==state.priority){
                                Color.White
                            }
                            else{
                                Color.Transparent
                            },
                            labelColor = if(priority==state.priority){
                                Color.White
                            }
                            else{
                                Color.White.copy(alpha = 0.7f)
                            },
                            onCLick = {onEvent(TaskEvent.OnPriorityChange(priority))}
                        )
                }
            }
            Spacer(Modifier.height(30.dp))
            Text(
                text = "Related to Subject"
                ,style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                val firstSubject = state.subjects.firstOrNull()?.name?:""
                Text(
                    text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isBottomSheetOpen = true}){
                    Icon(imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject")
                }
            }
            Button(enabled = taskTitleError==null, onClick = {onEvent(TaskEvent.SaveTask)}, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)) {
                Text("Save")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun taskScreenTopBar(
    isTaskExist : Boolean,
    isComplete : Boolean,
    checkBoxBorderColor : Color,
    onBackButtonClick : ()->Unit,
    onCheckBoxClick : ()->Unit,
    onDeleteButtonClick : ()->Unit,
    ){
    TopAppBar(
        title = {
            Text(
                text = "Task",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackButtonClick )
            {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back")
            }
        },
        actions = {
            if(isTaskExist){
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )

                IconButton(onClick = onDeleteButtonClick )
                {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = "Delete task")
                }
            }
        }
    )
}
@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label : String,
    bgColor : Color,
    borderColor : Color,
    labelColor : Color,
    onCLick : ()->Unit
){
    Box(
        modifier = modifier
            .background(bgColor)
            .clickable { onCLick() }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ){
        Text(text = label, color = labelColor)
    }
}