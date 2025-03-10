package org.nam.namnative.mediaplayer

import kotlin.time.Duration

data class MediaPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val recordTitle: String = "",
    val duration: Duration = Duration.ZERO,
    val currentPosition: Duration = Duration.ZERO
)
