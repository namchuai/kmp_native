package org.nam.namnative.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import models.RecordingState
import repositories.RecorderRepository

class RecordViewModel(
    private val recorderRepository: RecorderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RecordState())
    val state: StateFlow<RecordState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun startRecording(permissionsController: PermissionsController) {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.RECORD_AUDIO)
                if (state.value.recordingState == RecordingState.IDLE ||
                    state.value.recordingState == RecordingState.STOPPED
                ) {
                    try {
                        _state.update {
                            it.copy(
                                recordingTimeMs = 0L,
                                recordingState = RecordingState.RECORDING,
                                errorMessage = null
                            )
                        }

                        recorderRepository.startRecording()
                        startTimer()
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                recordingState = RecordingState.ERROR,
                                errorMessage = "Failed to start recording: ${e.message}"
                            )
                        }
                    }
                }
            } catch (e: DeniedAlwaysException) {
                permissionsController.openAppSettings()
            } catch (e: DeniedException) {
                e.printStackTrace()
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Stop recording and save file
     *
     * @return Path to the saved audio file, or null if error
     */
    fun stopRecording(): String? {
        return if (state.value.recordingState == RecordingState.RECORDING) {
            stopTimer()
            val filePath = recorderRepository.stopRecording()

            _state.update {
                it.copy(
                    recordingState = RecordingState.STOPPED,
                    filePath = filePath,
                    errorMessage = if (filePath == null) "Failed to save recording" else null
                )
            }

            filePath
        } else {
            null
        }
    }

    /**
     * Start timer to track recording duration
     */
    private fun startTimer() {
        stopTimer()

        timerJob = viewModelScope.launch {
            val initialTime = state.value.recordingTimeMs
            var elapsedTime = 0L

            while (isActive) {
                delay(100)
                elapsedTime += 100
                _state.update { it.copy(recordingTimeMs = initialTime + elapsedTime) }
            }
        }
    }

    /**
     * Stop timer
     */
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Clean up resources
     * Automatically called when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        stopTimer()
        recorderRepository.release()
    }
}
