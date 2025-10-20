package com.example.spotifyclone.Compos

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch


// Data class para canciones de la playlist
data class PlaylistSong(
    val id: String = "",
    val titulo: String = "",
    val artista: String = "",
    val audioUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    uid: String?, // ← Ahora puede ser nulo
    playlistId: String,
    name: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var canciones by remember { mutableStateOf<List<PlaylistSong>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf<PlaylistSong?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ Validar UID antes de usarlo
    LaunchedEffect(uid, playlistId) {
        if (uid.isNullOrBlank()) {
            Toast.makeText(context, "Usuario no encontrado o ID inválido", Toast.LENGTH_LONG).show()
            return@LaunchedEffect
        }

        db.collection("usuarios").document(uid)
            .collection("playlists").document(playlistId)
            .collection("canciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error al cargar playlist", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    canciones = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PlaylistSong::class.java)?.copy(id = doc.id)
                    }
                } else {
                    canciones = emptyList()
                }
            }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF121212), Color(0xFF000000))
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (canciones.isEmpty()) 0.7f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    Scaffold(
        topBar = {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF1DB954), Color(0xFF121212))
            )
            Surface(shadowElevation = 8.dp, color = Color.Transparent) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(16.dp)
                .alpha(alphaAnim)
        ) {
            if (uid.isNullOrBlank()) {
                // ⚠️ Mostrar mensaje si no hay UID
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudo cargar esta playlist (usuario inválido)", color = Color.Gray)
                }
                return@Column
            }

            Text("Playlist: $name", color = Color.White, fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))

            if (canciones.isEmpty()) {
                Text("No hay canciones en esta playlist", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(canciones) { cancion ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
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

                                // Menú de opciones
                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "Opciones",
                                            tint = Color.White
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Eliminar canción") },
                                            onClick = {
                                                expanded = false
                                                showDeleteDialog = cancion
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para eliminar canción
    if (showDeleteDialog != null) {
        val cancion = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    if (!uid.isNullOrBlank()) {
                        db.collection("usuarios").document(uid)
                            .collection("playlists").document(playlistId)
                            .collection("canciones").document(cancion.id)
                            .delete()
                    }
                    showDeleteDialog = null
                    scope.launch { snackbarHostState.showSnackbar("Canción eliminada correctamente") }
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
            text = {
                Text(
                    "¿Deseas eliminar \"${cancion.titulo}\" de la playlist?",
                    color = Color.LightGray
                )
            },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

