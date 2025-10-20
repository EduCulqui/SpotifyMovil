package com.example.spotifyclone.Compos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

// 🔹 Modelo de canción de playlist
data class PlaylistSong(
    val id: String = "",
    val titulo: String = "",
    val artista: String = "",
    val audioUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    uid: String?,
    playlistId: String,
    name: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var canciones by remember { mutableStateOf<List<PlaylistSong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf<PlaylistSong?>(null) }

    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val currentIndex by AudioPlayerManager.currentIndexFlow.collectAsState()
    val currentPlaylist by AudioPlayerManager.currentPlaylistFlow.collectAsState()

    // 🔄 Cargar canciones de Firestore en tiempo real
    LaunchedEffect(uid, playlistId) {
        if (uid.isNullOrBlank()) {
            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        db.collection("usuarios").document(uid)
            .collection("playlists").document(playlistId)
            .collection("canciones")
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                isLoading = false
                if (error != null) {
                    Toast.makeText(context, "Error al cargar playlist", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                canciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PlaylistSong::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
    }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1DB954), Color(0xFF121212))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(gradient)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1DB954))
                        Spacer(Modifier.height(12.dp))
                        Text("Cargando playlist...", color = Color.Gray)
                    }
                }

                canciones.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay canciones en esta playlist",
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    val urls = canciones.map { it.audioUrl }
                    val titles = canciones.map { it.titulo }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 🔊 Reproducir toda la playlist
                        Button(
                            onClick = {
                                if (urls.isNotEmpty()) {
                                    AudioPlayerManager.setPlaylist(
                                        context = context,
                                        urls = urls,
                                        songTitles = titles,
                                        startIndex = 0,
                                        playlistId = playlistId
                                    )
                                    Toast.makeText(context, "Reproduciendo playlist...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reproducir Playlist", color = Color.White)
                        }

                        Spacer(Modifier.height(16.dp))

                        // 🎵 Lista de canciones
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(canciones) { index, cancion ->
                                val isCurrentSong = currentPlaylist == playlistId && currentIndex == index

                                CancionPlaylistItem(
                                    cancion = cancion,
                                    isPlaying = isCurrentSong && isPlaying,
                                    onPlayClick = {
                                        if (cancion.audioUrl.isNotBlank()) {
                                            if (isCurrentSong) {
                                                AudioPlayerManager.playPause()
                                            } else {
                                                AudioPlayerManager.setPlaylist(
                                                    context,
                                                    urls,
                                                    titles,
                                                    startIndex = index,
                                                    playlistId = playlistId
                                                )
                                            }
                                        } else {
                                            Toast.makeText(context, "Sin audio disponible", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onDeleteClick = { showDeleteDialog = cancion }
                                )
                            }
                        }

                        PlayerControls() // 🎚️ Controles de reproducción al final
                    }
                }
            }
        }
    }

    // 🗑️ Diálogo de eliminar canción
    if (showDeleteDialog != null && uid != null) {
        val cancion = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseFirestore.getInstance()
                        .collection("usuarios").document(uid)
                        .collection("playlists").document(playlistId)
                        .collection("canciones").document(cancion.id)
                        .delete()
                    showDeleteDialog = null
                    Toast.makeText(context, "Canción eliminada", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            title = { Text("Eliminar canción", color = Color.White) },
            text = { Text("¿Deseas eliminar \"${cancion.titulo}\"?", color = Color.Gray) },
            containerColor = Color(0xFF1E1E1E)
        )
    }
}

// 🎵 Ítem de canción con botones
@Composable
fun CancionPlaylistItem(
    cancion: PlaylistSong,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    cancion.titulo,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    cancion.artista,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPlayClick) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = if (isPlaying) Color(0xFF1DB954) else Color.White
                    )
                }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Eliminar canción") },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
