package it.fast4x.rimusic.models

import androidx.compose.runtime.Immutable
import androidx.room.Embedded

@Immutable
data class SongPlaytime(
    @Embedded val song: Song,
    val songPlayTime: Long
)
