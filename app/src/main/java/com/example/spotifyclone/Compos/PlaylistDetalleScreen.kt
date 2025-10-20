package com.example.spotifyclone.Compos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spotifyclone.entities.Cancion
import com.example.spotifyclone.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetalleScreen(
    userId: String,
    playlistId: String,
    playlistNombre: String,
    onBack: () -> Unit
) {
    val repo = UserRepository()
    var canciones by remember { mutableStateOf(emptyList<Cancion>()) }

    // 🔹 Carga las canciones de Firestore al abrir la pantalla
    LaunchedEffect(playlistId) {
        canciones = repo.obtenerCancionesDePlaylist(userId, playlistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistNombre) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🎧 Loader con animación
            AnimatedVisibility(
                visible = canciones.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            }

            // 🎶 Lista de canciones con efecto
            AnimatedVisibility(
                visible = canciones.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(canciones) { cancion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Aquí podrías reproducir la canción o mostrar detalles */ },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = cancion.titulo,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = cancion.artista,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

