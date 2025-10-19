package com.example.spotifyclone.Compos

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AudioPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    private var currentIndex = 0
    private var playlist: List<String> = emptyList()
    private var titles: List<String> = emptyList()
    private var currentPlaylistId: String? = null
    private var isShuffle = false
    private var isRepeat = false

    // Estados observables
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentIndexFlow = MutableStateFlow(-1)
    val currentIndexFlow = _currentIndexFlow.asStateFlow()

    private val _currentTitle = MutableStateFlow("")
    val currentTitle = _currentTitle.asStateFlow()

    private val _currentPlaylistFlow = MutableStateFlow<String?>(null)
    val currentPlaylistFlow = _currentPlaylistFlow.asStateFlow()

    fun initPlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _currentIndexFlow.value = currentIndex
                        updateCurrentTitle()
                    }
                })
            }
        }
    }

    fun setPlaylist(
        context: Context,
        urls: List<String>,
        songTitles: List<String>,
        startIndex: Int = 0,
        playlistId: String? = null
    ) {
        initPlayer(context)
        playlist = urls
        titles = songTitles
        currentIndex = startIndex
        currentPlaylistId = playlistId
        _currentPlaylistFlow.value = playlistId
        _currentIndexFlow.value = startIndex
        playCurrent()
    }

    private fun playCurrent() {
        exoPlayer?.apply {
            stop()
            clearMediaItems()
            if (playlist.isNotEmpty()) {
                val url = playlist[currentIndex]
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                play()
                _isPlaying.value = true
                updateCurrentTitle()
            }
        }
    }

    private fun updateCurrentTitle() {
        _currentTitle.value = titles.getOrNull(currentIndex) ?: ""
    }

    fun playPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun stop() {
        exoPlayer?.pause()
        exoPlayer?.seekTo(0)
        _isPlaying.value = false
    }

    fun next() {
        if (playlist.isNotEmpty()) {
            currentIndex = if (isShuffle) (playlist.indices).random() else (currentIndex + 1) % playlist.size
            _currentIndexFlow.value = currentIndex
            playCurrent()
        }
    }

    fun previous() {
        if (playlist.isNotEmpty()) {
            currentIndex = if (currentIndex == 0) playlist.size - 1 else currentIndex - 1
            _currentIndexFlow.value = currentIndex
            playCurrent()
        }
    }

    fun toggleShuffle() { isShuffle = !isShuffle }
    fun toggleRepeat() {
        isRepeat = !isRepeat
        exoPlayer?.repeatMode =
            if (isRepeat) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    }

    fun isPlaying(): Boolean = _isPlaying.value

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun clearCurrentSong() {
        _currentTitle.value = ""
    }
}
