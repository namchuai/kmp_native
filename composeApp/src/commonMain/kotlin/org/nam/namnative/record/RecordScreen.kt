package org.nam.namnative.record

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.nam.namnative.koinViewModel
import models.RecordingState

@Composable
fun RecordScreen(onBackClicked: () -> Unit) {
    val viewModel = koinViewModel<RecordViewModel>()
    val state by viewModel.state.collectAsState()

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) {
        factory.createPermissionsController()
    }

    BindEffect(controller)

    val recordingPulse = animateFloatAsState(
        targetValue = if (state.recordingState == RecordingState.RECORDING) 1.2f else 1.0f,
        label = "Recording Pulse"
    )

    if (state.showSaveDialog) {
        SaveRecordingDialog(
            initialFileName = state.fileName ?: "",
            onDismiss = { viewModel.dismissSaveDialog() },
            onSave = { fileName -> viewModel.saveRecording(fileName) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TopAppBar(
            title = { Text("Record") },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.formattedTime,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = when (state.recordingState) {
                    RecordingState.RECORDING -> MaterialTheme.colors.error
                    RecordingState.PAUSED -> MaterialTheme.colors.secondary
                    else -> MaterialTheme.colors.onBackground
                },
                modifier = Modifier.scale(recordingPulse.value)
            )

            Text(
                text = when (state.recordingState) {
                    RecordingState.IDLE -> "Ready to record"
                    RecordingState.RECORDING -> "Recording..."
                    RecordingState.PAUSED -> "Paused"
                    RecordingState.STOPPED -> "Recording complete"
                    RecordingState.ERROR -> "Error"
                },
                color = when (state.recordingState) {
                    RecordingState.RECORDING -> MaterialTheme.colors.error
                    RecordingState.PAUSED -> MaterialTheme.colors.secondary
                    else -> MaterialTheme.colors.onSurface
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            if (state.recordingState == RecordingState.STOPPED && state.filePath != null) {
                Text(
                    text = "Saved to: ${state.filePath}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    viewModel.startRecording(controller)
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
                    .padding(16.dp),
                enabled = state.canStartRecording
            ) {
                Text("Start", color = Color.White)
            }

            IconButton(
                onClick = { viewModel.stopRecording() },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.error)
                    .padding(16.dp),
                enabled = state.canStopRecording
            ) {
                Text("Stop", color = Color.White)
            }
        }
    }
}

@Composable
fun SaveRecordingDialog(
    initialFileName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fileName by remember { mutableStateOf(initialFileName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Recording") },
        text = {
            Column {
                Text(
                    "Enter a name for your recording:",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Recording Name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fileName) },
                enabled = fileName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
