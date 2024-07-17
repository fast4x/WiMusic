package it.fast4x.rimusic.utils

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
@Composable
fun ApplyDiscoverToQueue(){
    /*   DISCOVER  */
    val discoverIsEnabled by rememberPreference(discoverKey, false)
    if (!discoverIsEnabled) return

    val binder = LocalPlayerServiceBinder.current

    binder?.player ?: return

    val player = binder.player

    var listMediaItemsIndex = remember {
        mutableListOf<Int>()
    }
    val windows by remember {
        mutableStateOf(player.currentTimeline.windows)
    }

    var songInPlaylist by remember {
        mutableStateOf(0)
    }
    var songIsLiked by remember {
        mutableStateOf(0)
    }
    if (discoverIsEnabled) {
        LaunchedEffect(Unit) {
            listMediaItemsIndex.clear()
            windows.forEach { window ->
                withContext(Dispatchers.IO) {
                    songInPlaylist = Database.songUsedInPlaylists(window.mediaItem.mediaId)
                    songIsLiked = Database.songliked(window.mediaItem.mediaId)
                }
                if (songInPlaylist > 0 || songIsLiked > 0) {
                    listMediaItemsIndex.add(window.firstPeriodIndex)
                }
            }.also {
                if (listMediaItemsIndex.isNotEmpty()) {
                    val mediacount = listMediaItemsIndex.size - 1
                    listMediaItemsIndex.sort()
                    for (i in mediacount.downTo(0)) {
                        binder.player.removeMediaItem(listMediaItemsIndex[i])
                    }
                    listMediaItemsIndex.clear()
                }
            }

        }
    }
    /*   DISCOVER  */

}