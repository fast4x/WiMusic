package it.fast4x.rimusic.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.platform.LocalContext

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadService

import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalDownloader
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.service.MyDownloadHelper
import it.fast4x.rimusic.service.MyDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun InitDownloader() {
    val context = LocalContext.current
    MyDownloadHelper.getDownloadManager(context)
    MyDownloadHelper.getDownloads()
}


@UnstableApi
@Composable
fun downloadedStateMedia(mediaId: String): Boolean {
    val binder = LocalPlayerServiceBinder.current

    val cachedBytes by remember(mediaId) {
        mutableStateOf(binder?.cache?.getCachedBytes(mediaId, 0, -1))
    }

    var format by remember {
        mutableStateOf<Format?>(null)
    }

    var isDownloaded by remember { mutableStateOf(false) }
    LaunchedEffect(mediaId) {
        MyDownloadHelper.getDownload(mediaId).collect { download ->
            isDownloaded = download?.state == Download.STATE_COMPLETED
        }
    }

    LaunchedEffect(mediaId) {
        Database.format(mediaId).distinctUntilChanged().collectLatest { currentFormat ->
            format = currentFormat
        }
    }

    return (format?.contentLength == cachedBytes) || isDownloaded
}


@UnstableApi
fun manageDownload(
    context: android.content.Context,
    mediaItem: MediaItem,
    downloadState: Boolean = false
) {

    if (downloadState)
        DownloadService.sendRemoveDownload(
            context,
            MyDownloadService::class.java,
            mediaItem.mediaId,
            false
        )
    else {
        if (isNetworkAvailable(context)) {

            MyDownloadHelper.scheduleDownload(context = context, mediaItem = mediaItem)


        }
    }

}


@UnstableApi
@Composable
fun getDownloadState(mediaId: String): Int {
    val downloader = LocalDownloader.current
    //if (!checkInternetConnection()) return 3
    if (!isNetworkAvailableComposable()) return 3

    return downloader.getDownload(mediaId).collectAsState(initial = null).value?.state
        ?: 3
}

