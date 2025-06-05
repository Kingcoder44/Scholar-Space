package com.example.bookbeacon.ui.presentation.Subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookbeacon.ui.presentation.component.AddSubjectDialog
import com.example.bookbeacon.ui.presentation.component.CountCard
import com.example.bookbeacon.ui.presentation.component.DeleteDialog
import com.example.bookbeacon.ui.presentation.component.studySessionsList
import com.example.bookbeacon.ui.presentation.component.tasksList
import com.example.bookbeacon.ui.presentation.destinations.TaskScreenRouteDestination
import com.example.bookbeacon.ui.presentation.task.TaskScreenNavArgs
import com.example.scholar_space.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

data class SubjectScreenNavArgs(
    val subjectId : Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
)
{

    val viewModel : SubjectVM = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SubjectScreen(
        state = state,
        snackbarEvent = viewModel.snackBarEventFlow,
        onEvent = viewModel::onEvent,
        onBackIconClicked = { navigator.navigateUp() },
        onAddTaskButtonClciked = {
            val navArg = TaskScreenNavArgs(
                TaskId = null,
                subjectId = state.currentSubjectId
            )
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClicked = {
            taskId ->
            val navArg = TaskScreenNavArgs(
                TaskId = taskId,
                subjectId = null
            )
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg)) }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(

    state:SubjectState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (SubjectEvent)->Unit,
    onBackIconClicked: () -> Unit,
    onAddTaskButtonClciked: ()->Unit,
    onTaskCardClicked : (Int?)->Unit
)
{
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFabExtended by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0}
    }
    var isEditSubjectOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
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

                SnackbarEvent.NavigateUp -> onBackIconClicked()
            }
        }
    }
    LaunchedEffect(key1 = state.studiedHours,key2=state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)
    }
    AddSubjectDialog(
        isopen = isEditSubjectOpen,
        onDismissRequest = { isEditSubjectOpen = false },
        onConfirmButton = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectOpen = false },
        selectedColor = state.subjectCardColors,
        onColorChange = { onEvent(SubjectEvent.OnSubjectCardColorChange(it))},
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = {onEvent(SubjectEvent.OnSubjectNameChange(it))},
        onGoalHourChange = {onEvent(SubjectEvent.OnGoalStudyHoursChange(it))},
    )
    DeleteDialog(
        isopen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session?",
        onDismissRequest = { isDeleteDialogOpen = false},
        onConfirmButton = {
            onEvent(SubjectEvent.DeleteSession)
            isDeleteDialogOpen = false


        }
    )
    //subject deletion
    DeleteDialog(
        isopen = isDeleteSubjectDialogOpen,
        title = "Delete Subject?",
        bodyText = "Are you sure, you want to delete this subject?",
        onDismissRequest = { isDeleteSubjectDialogOpen = false},
        onConfirmButton = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogOpen = false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topAppBar(
            title = state.subjectName,
            onBackIconClicked = onBackIconClicked,
            onDeleteIconClicked = {isDeleteSubjectDialogOpen = true},
            onEditIconClicked = {isEditSubjectOpen=true},
            scrollBehavior = scrollBehavior
        ) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClciked,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add new task.") },
                text =  { Text("Add Task") },
                expanded = isFabExtended,

            )
        }
    ) {
        paddingValue->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ){
            item{
            subjectOverviewSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                studiedHours = state.studiedHours.toString(),
                goalHours = state.goalStudyHours,
                progress = state.progress
            )
            }
            tasksList(
                sectionTitle = "UPCOMING TASKS", tasks = state.upcomingTasks,
                emptyListText = "You don't have any upcoming tasks.\nClick the + button in subject screen to add new task.",
                onCheckboxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClicked

            )

            tasksList(
                sectionTitle = "COMPLETED TASKS", tasks = state.completedTasks,
                emptyListText = "You don't have any completed task tasks.\nClick the checkbox to mark completetion of task.",
                onCheckboxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClicked

            )
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\nStart a study session to start recording your progress.",
                session = state.recentSessions,
                onDeleteIconCLick = {
                    onEvent(SubjectEvent.OnDeleteSessionButtonClick(it))
                    isDeleteDialogOpen =true   }
            )

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun topAppBar(
    title : String,
    onDeleteIconClicked : ()->Unit,
    onBackIconClicked : ()->Unit,
    onEditIconClicked : ()->Unit,
    scrollBehavior: TopAppBarScrollBehavior
){
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackIconClicked)
            {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back")
            }
        },
        actions = {
            IconButton(onClick =onDeleteIconClicked)
            {
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject")
            }
            IconButton(onClick = onEditIconClicked)
            {
                Icon(imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Subject")
            }
        }
    )
}
@Composable
private fun subjectOverviewSection(
    modifier: Modifier,
    studiedHours : String,
    goalHours : String,
    progress : Float
)
{
    val percentage = remember(progress){
        (progress*100).toInt().coerceIn(0,100)
    }
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )
        Spacer(Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(text = "$percentage%")
        }

    }
}
