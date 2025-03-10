package org.nam.namnative.record

import models.RecordingState

data class RecordState(
    val recordingState: RecordingState = RecordingState.IDLE,
    val recordingTimeMs: Long = 0L,
    val filePath: String? = null,
    val errorMessage: String? = null,
    val showSaveDialog: Boolean = false,
    val fileName: String? = null,
) {
    val formattedTime: String
        get() {
            val totalSeconds = recordingTimeMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }

    val canStartRecording: Boolean
        get() = recordingState == RecordingState.IDLE ||
                recordingState == RecordingState.STOPPED

    val canStopRecording: Boolean
        get() = recordingState == RecordingState.RECORDING ||
                recordingState == RecordingState.PAUSED
}
