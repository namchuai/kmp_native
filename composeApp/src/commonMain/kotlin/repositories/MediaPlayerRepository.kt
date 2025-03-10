package repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import models.MediaPlayingState
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MediaPlayerRepository {
    val playingState: Flow<MediaPlayingState>

    val duration: Flow<Long>

    val currentPosition: Flow<Long>

    val trackTitle: Flow<String>

    suspend fun prepare(filePath: String, dispatcher: CoroutineContext = Dispatchers.IO)

    fun play()

    fun pause()

    fun release()

    fun seekTo(position: Long)
}