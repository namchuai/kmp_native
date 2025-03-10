package repositories

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.MediaPlayingState
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MediaPlayerRepository {

    private val _playingState = MutableStateFlow(MediaPlayingState.IDLE)
    actual val playingState: Flow<MediaPlayingState> = _playingState.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    actual val duration: Flow<Long> = _duration.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    actual val currentPosition: Flow<Long> = _currentPosition.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private val _trackTitle = MutableStateFlow("")
    actual val trackTitle: Flow<String> = _trackTitle.asStateFlow()

    private var positionUpdateJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    actual fun play() {
        mediaPlayer?.let {
            it.start()
            _playingState.value = MediaPlayingState.PLAYING
            startPositionTracking()
        }
    }

    actual fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playingState.value = MediaPlayingState.PAUSED
            }
        }
    }

    actual fun release() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        _playingState.value = MediaPlayingState.IDLE
        _currentPosition.value = 0
        _duration.value = 0
        _trackTitle.value = ""
    }

    actual suspend fun prepare(filePath: String, dispatcher: CoroutineContext) {
        withContext(dispatcher) {
            release()
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(filePath)
                    setOnPreparedListener {
                        _duration.value = duration.toLong()
                        _playingState.value = MediaPlayingState.PAUSED
                    }
                    setOnCompletionListener {
                        _playingState.value = MediaPlayingState.COMPLETED
                        _currentPosition.value = _duration.value
                    }
                    setOnErrorListener { _, errorCode, extra ->
                        Log.e(TAG, "Media player error: code $errorCode, extra $extra")
                        _playingState.value = MediaPlayingState.ERROR
                        true
                    }
                    prepare()
                }
            } catch (e: Exception) {
                _playingState.value = MediaPlayingState.ERROR
                Log.e(TAG, "Error preparing media player", e)
            }
        }
    }

    actual fun seekTo(position: Long) {
        mediaPlayer?.let {
            it.seekTo(position.toInt())
            _currentPosition.value = position

            if (_playingState.value == MediaPlayingState.COMPLETED) {
                _playingState.value = MediaPlayingState.PAUSED
            }
        }
    }

    private fun startPositionTracking() {
        stopPositionTracking()
        positionUpdateJob = coroutineScope.launch {
            while (isActive && _playingState.value == MediaPlayingState.PLAYING) {
                try {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            _currentPosition.value = player.currentPosition.toLong()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating position", e)
                }

                delay(100)
            }
        }
    }

    private fun stopPositionTracking() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    companion object {
        private const val TAG = "MediaPlayerRepository"
    }
}