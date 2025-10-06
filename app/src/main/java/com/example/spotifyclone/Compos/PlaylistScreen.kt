package com.example.spotifyclone.Compos

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.R
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.viewmodels.AlbumViewModel
import com.google.firebase.firestore.FirebaseFirestore


// Data class para canciones de la playlist
data class PlaylistSong(
    val id: String = "",
    val titulo: String = "",
    val artista: String = ""
)

// Pantalla de la playlist
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

    // Escuchar canciones en esta playlist
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

    // Fondo con gradiente tipo Spotify
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF121212), Color(0xFF000000))
    )

    // Animación de opacidad al entrar
    val alphaAnim by animateFloatAsState(
        targetValue = if (canciones.isEmpty()) 0.7f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    Scaffold(
        // 🟢 NUEVO: TopBar con fondo animado, sombra y mini portada
        topBar = {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF1DB954), Color(0xFF121212))
            )

            Surface(
                shadowElevation = 8.dp,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gradientBrush)
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // portada redonda animada tipo Spotify
                        val infiniteTransition = rememberInfiniteTransition()
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(10000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                name,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tu lista personalizada",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },

        floatingActionButton = {
            // animación suave al mostrar FAB
            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF1DB954),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar canción", tint = Color.White)
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(16.dp)
                .alpha(alphaAnim) // efecto de entrada
        ) {
            Text("Playlist: $name", color = Color.White, fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))

            if (canciones.isEmpty()) {
                Text("No hay canciones aún", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(canciones) { cancion ->
                        // animación de elevación y brillo al presionar
                        var pressed by remember { mutableStateOf(false) }
                        val cardElevation by animateDpAsState(if (pressed) 12.dp else 4.dp)
                        val scale by animateFloatAsState(if (pressed) 0.97f else 1f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            pressed = true
                                            tryAwaitRelease()
                                            pressed = false
                                        }
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
                            elevation = CardDefaults.cardElevation(cardElevation)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    cancion.titulo,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    cancion.artista,
                                    color = Color(0xFFB3B3B3),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para agregar canciones desde álbum
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

    // Cargar canciones del álbum seleccionado
    LaunchedEffect(selectedAlbum) {
        albumViewModel.cargarCanciones(selectedAlbum)
    }

    // efecto de desenfoque al fondo
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .blur(4.dp)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Agregar canción a la playlist",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Selector de álbum
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                    ) {
                        Text("Álbum: $selectedAlbum", color = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = Color.DarkGray
                    ) {
                        albumOptions.forEach { album ->
                            DropdownMenuItem(
                                text = { Text(album, color = Color.White) },
                                onClick = {
                                    selectedAlbum = album
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de canciones del álbum seleccionado
                if (canciones.isEmpty()) {
                    Text("No hay canciones en este álbum aún", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
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
                                Text(
                                    "${cancion.titulo} - ${cancion.artista}",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF1DB954))
            }
        },
        containerColor = Color(0xFF121212)
    )
}

