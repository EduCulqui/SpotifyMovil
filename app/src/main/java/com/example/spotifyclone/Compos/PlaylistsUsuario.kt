package com.example.spotifyclone.Compos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spotifyclone.repository.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsUsuarioScreen(
    userId: String,
    onBack: () -> Unit,
    onAbrirPlaylist: (String, String) -> Unit // playlistId, nombre
) {
    val repo = UserRepository()
    var playlists by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        scope.launch {
            playlists = repo.obtenerPlaylistsDeUsuario(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playlists del usuario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (playlists.isEmpty()) {
                Text("No hay playlists disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(playlists) { playlist ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    val id = playlist["id"].toString()
                                    val nombre = playlist["nombre"].toString()
                                    onAbrirPlaylist(id, nombre)
                                }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(playlist["nombre"].toString(), style = MaterialTheme.typography.titleMedium)
                                Text(playlist["descripcion"]?.toString() ?: "")
                            }
                        }
                    }
                }
            }
        }
    }
}
