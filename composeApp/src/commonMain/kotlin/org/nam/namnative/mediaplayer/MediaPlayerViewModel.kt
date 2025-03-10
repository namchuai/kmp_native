package org.nam.namnative.mediaplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import models.MediaPlayingState
import repositories.MediaPlayerRepository
import repositories.RecordInfoRepository
import kotlin.time.Duration.Companion.milliseconds

class MediaPlayerViewModel(
    private val recordInfoRepository: RecordInfoRepository,
    private val mediaPlayerRepository: MediaPlayerRepository,
) : ViewModel() {

    private val _playerState = MutableStateFlow(MediaPlayerState())
    val playerState: StateFlow<MediaPlayerState> = _playerState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                mediaPlayerRepository.playingState,
                mediaPlayerRepository.duration,
                mediaPlayerRepository.currentPosition,
                mediaPlayerRepository.trackTitle,
            ) { playingState, duration, position, title ->
                MediaPlayerState(
                    isPlaying = playingState == MediaPlayingState.PLAYING,
                    isLoading = playingState == MediaPlayingState.PREPARING,
                    recordTitle = title,
                    duration = duration.milliseconds,
                    currentPosition = position.milliseconds
                )
            }.collect { newState ->
                _playerState.value = newState
            }
        }
    }

    fun play() {
        viewModelScope.launch {
            mediaPlayerRepository.play()
        }
    }

    fun pause() {
        viewModelScope.launch {
            mediaPlayerRepository.pause()
        }
    }

    fun seekTo(position: Float) {
        viewModelScope.launch {
            val newPositionMs =
                (position * _playerState.value.duration.inWholeMilliseconds).toLong()
            mediaPlayerRepository.seekTo(newPositionMs)
        }
    }

    fun loadRecord(recordId: String) {
        viewModelScope.launch {
            recordInfoRepository.getRecord(recordId)?.let {
                mediaPlayerRepository.prepare(it.path)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerRepository.release()
    }
}