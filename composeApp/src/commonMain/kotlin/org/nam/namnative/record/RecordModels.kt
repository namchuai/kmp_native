package org.nam.namnative.record

import models.RecordingState

data class RecordState(
    val recordingState: RecordingState = RecordingState.IDLE,
    val recordingTimeMs: Long = 0L,
    val filePath: String? = null,
    val errorMessage: String? = null
) {
    /**
     * Returns formatted time string in MM:SS format
     */
    val formattedTime: String
        get() {
            val totalSeconds = recordingTimeMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }

    /**
     * Returns true if recording is active (either RECORDING or PAUSED state)
     */
    val isRecordingActive: Boolean
        get() = recordingState == RecordingState.RECORDING ||
                recordingState == RecordingState.PAUSED

    /**
     * Returns true if recording can be started (either IDLE or STOPPED state)
     */
    val canStartRecording: Boolean
        get() = recordingState == RecordingState.IDLE ||
                recordingState == RecordingState.STOPPED

    /**
     * Returns true if recording can be paused (only in RECORDING state)
     */
    val canPauseRecording: Boolean
        get() = recordingState == RecordingState.RECORDING

    /**
     * Returns true if recording can be resumed (only in PAUSED state)
     */
    val canResumeRecording: Boolean
        get() = recordingState == RecordingState.PAUSED

    /**
     * Returns true if recording can be stopped (either RECORDING or PAUSED state)
     */
    val canStopRecording: Boolean
        get() = recordingState == RecordingState.RECORDING ||
                recordingState == RecordingState.PAUSED
}
