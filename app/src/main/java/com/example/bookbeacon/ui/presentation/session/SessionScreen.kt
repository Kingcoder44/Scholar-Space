package com.example.bookbeacon.ui.presentation.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookbeacon.ui.presentation.component.DeleteDialog
import com.example.bookbeacon.ui.presentation.component.SubjectListBottomSheet
import com.example.bookbeacon.ui.presentation.component.studySessionsList
import com.example.bookbeacon.ui.presentation.theme.Red
import com.example.scholar_space.ui.presentation.session.ServiceHelper
import com.example.scholar_space.ui.presentation.session.StudySessionTimerService
import com.example.scholar_space.ui.presentation.session.TimerState
import com.example.scholar_space.util.Constants.ACTION_SERVICE_CANCEL
import com.example.scholar_space.util.Constants.ACTION_SERVICE_START
import com.example.scholar_space.util.Constants.ACTION_SERVICE_STOP
import com.example.scholar_space.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "scholar_space://dashboard/session"
        )
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessionTimerService

) {

    val viewModel : SessionVM = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionScreen(
        state = state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        timerService = timerService,
        onBackButtonClick = { navigator.navigateUp()}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    state: SessionState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (SessionEvent)->Unit,
    onBackButtonClick: () -> Unit,
    timerService: StudySessionTimerService
) {

    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

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

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }
    LaunchedEffect(key1 = state.subjects) {
        val subjectId = timerService.subjectId.value
        onEvent(
            SessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedToSubject = state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }



    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects= state.subjects,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectClicked = { subject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        }
    )

    DeleteDialog(
        isopen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? " +
                "This action can not be undone.",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButton = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteDialogOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SessionScreenTopBar(onBackButtonClick = onBackButtonClick)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours, minutes, seconds
                )
            }
            item {
                RelatedToSubject(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject =state.relatedToSubject?:"",
                    selectedSubjectButtonClick = { isBottomSheetOpen = true },
                seconds = seconds
                    )
            }
            item {
                ButtonSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        if(state.subjectId!=null && state.relatedToSubject!=null)
                        {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = if(currentTimerState==TimerState.STARTED){
                                    ACTION_SERVICE_STOP
                                }else
                                    ACTION_SERVICE_START
                            )
                            timerService.subjectId.value = state.subjectId
                        }
                        else{
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                                             },
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    },
                    finishButtonCLick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if(duration>=36){

                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = ACTION_SERVICE_CANCEL
                            )
                        }
                        onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimerState,
            seconds = seconds
                )
            }
                studySessionsList(
                    sectionTitle = "STUDY SESSIONS HISTORY",
                    emptyListText = "You don't have any recent study sessions.\nStart a study session to start recording your progress.",
                    session = state.sessions,
                    onDeleteIconCLick = {
                        onEvent(SessionEvent.OnDeleteSessionButtonClick(it))
                        isDeleteDialogOpen=true }
                )

            }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate to Back Screen"
                )
            }
        },
        title = {
            Text(text = "Study Sessions", style = MaterialTheme.typography.headlineSmall)
        }
    )
}

@Composable
private fun TimerSection(
    modifier : Modifier,
    hours : String,
    minutes : String,
    seconds : String
){
Box(modifier = modifier,
    contentAlignment = Alignment.Center ){
    Box(Modifier
        .size(250.dp)
        .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape))

    Row {
        AnimatedContent(targetState = hours, label = hours, transitionSpec = { timerTextAnimation() }) {
                hours->
            Text(
                text = "$hours:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
            )
        }
        AnimatedContent(targetState = minutes, label = minutes, transitionSpec = { timerTextAnimation() }) {
                minutes->
            Text(
                text = "$minutes:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
            )
        }
        AnimatedContent(targetState = seconds, label = seconds, transitionSpec = { timerTextAnimation() }) {
                seconds->
            Text(
                text = seconds,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
            )
        }
    }

}
}
@Composable
private fun RelatedToSubject(
    modifier: Modifier,
    relatedToSubject : String,
    selectedSubjectButtonClick: ()->Unit,
    seconds : String
){
    Column(modifier = modifier){
        Text(
            text = "Related to Subject"
            ,style = MaterialTheme.typography.bodySmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = selectedSubjectButtonClick,
                enabled = seconds == "00"
            ){
                Icon(imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject")
            }
        }
    }
}
@Composable
private fun ButtonSection(
    modifier: Modifier,
    startButtonClick:()->Unit,
    cancelButtonClick : ()->Unit,
    finishButtonCLick : ()->Unit,
    timerState: TimerState,
    seconds: String
){
Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween
){
    Button(onClick = cancelButtonClick,
        enabled = seconds!="00" && timerState!=TimerState.STARTED) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp,5.dp),
            text = "Reset"
        )
    }
    Button(onClick = startButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if(timerState == TimerState.STARTED) Red else MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
        ) {
    Text(
        modifier = Modifier.padding(horizontal = 10.dp,5.dp),
        text = when(timerState){
            TimerState.STARTED -> "Stop"
            TimerState.STOPPED -> "Resume"
            else -> "Start"
        }
    )
    }
    Button(onClick = finishButtonCLick,
        enabled = seconds!="00" && timerState!=TimerState.STARTED) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp,5.dp),
            text = "Finish"
        )
    }
}
}
//to give aniamtion to timer text
private fun timerTextAnimation(duration: Int = 600) : ContentTransform{
    return slideInVertically(animationSpec = tween(duration)) { fullHeight ->fullHeight } + fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) {fullHeight -> -fullHeight  } + fadeOut(animationSpec = tween(duration))
}