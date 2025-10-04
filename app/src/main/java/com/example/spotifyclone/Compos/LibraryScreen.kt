package com.example.spotifyclone.Compos

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBack: () -> Unit,
    onOpenPlaylist: (String, String) -> Unit
) {
    val ctx = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val uid = auth.currentUser?.uid
    var playlists by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var showDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    // 🔹 Cargar playlists desde Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("usuarios")
                .document(uid)
                .collection("playlists")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Toast.makeText(ctx, "Error al cargar playlists", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        playlists = snapshot.documents.map { doc ->
                            doc.id to (doc.getString("name") ?: "Sin nombre")
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Biblioteca", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF121212),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212), // Fondo oscuro tipo Spotify
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF1DB954)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Playlist", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (playlists.isEmpty()) {
                Text("No tienes playlists todavía. ¡Crea la primera!", color = Color.Gray)
            } else {
                playlists.forEach { (id, name) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlaylist(id, name) }
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    // 🔹 Dialogo para crear playlist
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Playlist", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Nombre de la playlist") },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1DB954),
                        unfocusedBorderColor = Color.DarkGray,
                        disabledBorderColor = Color.DarkGray,
                        cursorColor = Color(0xFF1DB954),
                        focusedLabelColor = Color(0xFF1DB954),
                        unfocusedLabelColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        selectionColors = TextSelectionColors(
                            handleColor = Color(0xFF1DB954),
                            backgroundColor = Color(0xFF1DB954).copy(alpha = 0.3f)
                        )
                    )
                )



            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank() && uid != null) {
                        val newId = System.currentTimeMillis().toString()
                        val playlistData = mapOf("name" to newPlaylistName)

                        db.collection("usuarios")
                            .document(uid)
                            .collection("playlists")
                            .document(newId)
                            .set(playlistData)
                            .addOnSuccessListener {
                                Toast.makeText(ctx, "Playlist creada", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(ctx, "Error al guardar playlist", Toast.LENGTH_SHORT).show()
                            }

                        newPlaylistName = ""
                        showDialog = false
                    }
                }) {
                    Text("Crear", color = Color(0xFF1DB954))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newPlaylistName = ""
                    showDialog = false
                }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
}


