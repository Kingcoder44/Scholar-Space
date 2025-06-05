package com.example.bookbeacon.ui.presentation.component

import android.view.Display.Mode
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bookbeacon.R
import com.example.bookbeacon.domain.model.Task
import com.example.scholar_space.util.Priority
import com.example.scholar_space.util.changeMillisToDateString

fun LazyListScope.tasksList(
    sectionTitle : String,
    tasks : List<Task>,
    emptyListText : String,
    onTaskCardClick: (Int?)->Unit,
    onCheckboxClick: (Task)->Unit
){
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
    if(tasks.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.tasks),
                    contentDescription = emptyListText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    items(tasks){task->
        TaskCard(modifier = Modifier.padding(12.dp,4.dp),
            task = task,
            onCheckBoxClick = {onCheckboxClick(task)},
            onClick ={onTaskCardClick(task.taskId)})
    }
}


@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckBoxClick: () -> Unit,
    onClick : () -> Unit
)
{
    ElevatedCard(modifier.clickable {onClick() }
    ) {
        Row (modifier = Modifier.fillMaxWidth().
            padding(8.dp),
            verticalAlignment = Alignment.CenterVertically){
            TaskCheckBox(
                isComplete = task.isComplete,
                borderColor = Priority.fromInt(task.priority).color,
                onCheckBoxClick = onCheckBoxClick
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column{
                Text(
                    text = task.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,//places ... at end in case of overflow
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if(task.isComplete){
                        TextDecoration.LineThrough
                    }
                    else
                    {
                        TextDecoration.None
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }

}