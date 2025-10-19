package com.example.spotifyclone.Compos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.spotifyclone.entities.Cancion
import com.example.spotifyclone.viewmodels.AlbumViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    albumId: String,
    albumName: String,
    onBack: () -> Unit,
    viewModel: AlbumViewModel = viewModel()
) {
    val canciones by viewModel.canciones
    val isLoading by viewModel.isLoading
    val ctx = LocalContext.current

    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val currentIndex by AudioPlayerManager.currentIndexFlow.collectAsState()
    val currentPlaylist by AudioPlayerManager.currentPlaylistFlow.collectAsState()

    LaunchedEffect(albumId) {
        if (albumId.isNotBlank()) {
            viewModel.cargarCanciones(albumId)
        }
    }

    val alphaAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = albumName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .alpha(alphaAnim.value)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(3) {
                            ShimmerPlaceholder()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                canciones.isEmpty() -> {
                    Text(
                        text = "No hay canciones en este álbum.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    val urls = canciones.map { it.audioUrl }
                    val titles = canciones.map { it.titulo }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(canciones) { index, cancion ->
                                val isCurrentSong = currentPlaylist == albumId && currentIndex == index

                                CancionItem(
                                    cancion = cancion,
                                    index = index,
                                    isPlaying = isCurrentSong && isPlaying,
                                    onPlayClick = {
                                        if (cancion.audioUrl.isNotBlank()) {
                                            if (isCurrentSong) {
                                                AudioPlayerManager.playPause()
                                            } else {
                                                AudioPlayerManager.setPlaylist(
                                                    ctx,
                                                    urls,
                                                    titles,
                                                    startIndex = index,
                                                    playlistId = albumId
                                                )
                                            }
                                        } else {
                                            Toast.makeText(ctx, "Sin audio disponible", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }

                        PlayerControls()
                    }
                }
            }
        }
    }
}



// Ítem con efecto al presionar (escala + sombra)
@Composable
fun CancionItem(
    cancion: Cancion,
    index: Int,
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 🎵 Título y artista
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cancion.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = cancion.artista,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // ▶️ / ⏸️ Botón de reproducción sincronizado con AudioPlayerManager
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}





// Placeholder con efecto shimmer tipo Spotify
@Composable
fun ShimmerPlaceholder() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 200f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(brush, shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun PlayerControls() {
    val ctx = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isShuffle by remember { mutableStateOf(false) }
    var isRepeat by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            AudioPlayerManager.toggleShuffle()
            isShuffle = !isShuffle
        }) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Aleatorio",
                tint = if (isShuffle) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }

        IconButton(onClick = { AudioPlayerManager.previous() }) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior")
        }

        IconButton(onClick = {
            AudioPlayerManager.playPause()
            isPlaying = !isPlaying
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        IconButton(onClick = { AudioPlayerManager.next() }) {
            Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
        }

        IconButton(onClick = {
            AudioPlayerManager.toggleRepeat()
            isRepeat = !isRepeat
        }) {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = "Repetir",
                tint = if (isRepeat) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

