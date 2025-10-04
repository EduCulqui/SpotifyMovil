package com.example.spotifyclone.Compos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.viewmodels.AlbumViewModel
import com.google.firebase.firestore.FirebaseFirestore

// 🔹 Data class para canciones de la playlist
data class PlaylistSong(
    val id: String = "",
    val titulo: String = "",
    val artista: String = ""
)

// 🔹 Pantalla de la playlist
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    uid: String,
    playlistId: String,
    name: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val albumVM: AlbumViewModel = viewModel()

    var canciones by remember { mutableStateOf<List<PlaylistSong>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    // 🔹 Escuchar canciones en esta playlist
    LaunchedEffect(playlistId) {
        db.collection("usuarios").document(uid)
            .collection("playlists").document(playlistId)
            .collection("canciones")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    canciones = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PlaylistSong::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.Green
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar canción")
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Text("Playlist: $name", color = Color.White, fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))

            if (canciones.isEmpty()) {
                Text("No hay canciones aún", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(canciones) { cancion ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(cancion.titulo, color = Color.White, fontSize = 18.sp)
                                Text(cancion.artista, color = Color.LightGray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔹 Diálogo para agregar canciones desde álbum
    if (showAddDialog) {
        AddSongDialog(
            albumViewModel = albumVM,
            onDismiss = { showAddDialog = false },
            onSongSelected = { titulo, artista ->
                val nuevaCancion = mapOf(
                    "titulo" to titulo,
                    "artista" to artista
                )
                db.collection("usuarios").document(uid)
                    .collection("playlists").document(playlistId)
                    .collection("canciones")
                    .add(nuevaCancion)
                showAddDialog = false
            }
        )
    }
}

// 🔹 Diálogo para seleccionar canción desde un álbum
@Composable
fun AddSongDialog(
    albumViewModel: AlbumViewModel = viewModel(),
    onDismiss: () -> Unit,
    onSongSelected: (String, String) -> Unit
) {
    val ctx = LocalContext.current
    var selectedAlbum by remember { mutableStateOf("rock") }
    val albumOptions = listOf(
        "rock", "pop", "cumbia", "reggaeton_2023",
        "baladas", "salsa", "pachanga", "perreo", "rap"
    )

    val canciones = albumViewModel.canciones.value

    // 🔹 Cargar canciones del álbum seleccionado
    LaunchedEffect(selectedAlbum) {
        albumViewModel.cargarCanciones(selectedAlbum)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar canción a la playlist") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 🔹 Selector de álbum
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text("Álbum: $selectedAlbum")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        albumOptions.forEach { album ->
                            DropdownMenuItem(
                                text = { Text(album) },
                                onClick = {
                                    selectedAlbum = album
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Lista de canciones del álbum seleccionado
                if (canciones.isEmpty()) {
                    Text("No hay canciones en este álbum aún", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp) // limita altura
                    ) {
                        items(canciones) { cancion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSongSelected(cancion.titulo, cancion.artista)
                                        Toast.makeText(
                                            ctx,
                                            "Canción agregada: ${cancion.titulo}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(8.dp)
                            ) {
                                Text("${cancion.titulo} - ${cancion.artista}", color = Color.White)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

