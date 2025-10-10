package com.example.spotifyclone.Compos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.entities.Cancion
import com.example.spotifyclone.viewmodels.BuscarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(
    onBack: () -> Unit,
    viewModel: BuscarViewModel = viewModel()
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    val resultados by viewModel.resultados
    val isLoading by viewModel.isLoading
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFF121212),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Canción o artista") },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1DB954),
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color(0xFF1DB954),
                        selectionColors = TextSelectionColors(
                            handleColor = Color(0xFF1DB954),
                            backgroundColor = Color(0xFF1DB954).copy(alpha = 0.3f)
                        )
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.buscar(query.text) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF1DB954)
                )
                resultados.isEmpty() -> Text(
                    "No se encontraron resultados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )


                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(resultados) { cancion ->
                            ResultadoItem(
                                cancion = cancion,
                                onAddToPlaylist = { playlistId, playlistName ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Canción agregada a \"$playlistName\" exitosamente"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultadoItem(
    cancion: Cancion,
    onAddToPlaylist: (String, String) -> Unit // ✅ SIN @Composable AQUÍ
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    cancion.titulo,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    cancion.artista,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

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
                    text = { Text("Reproducir") },
                    onClick = {
                        expanded = false
                        // Aquí iría tu lógica de reproducción
                    }
                )
                DropdownMenuItem(
                    text = { Text("Agregar a playlist") },
                    onClick = {
                        expanded = false
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog) {
        DialogAgregarAPlaylist(
            cancion = cancion,
            onDismiss = { showDialog = false },
            onAddToPlaylist = onAddToPlaylist
        )
    }
}


@Composable
fun DialogAgregarAPlaylist(
    cancion: Cancion,
    onDismiss: () -> Unit,
    onAddToPlaylist: (String, String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var playlists by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    // Cargar playlists del usuario
    LaunchedEffect(Unit) {
        if (uid != null) {
            val snapshot = db.collection("usuarios")
                .document(uid)
                .collection("playlists")
                .get()
                .await()

            playlists = snapshot.documents.mapNotNull {
                val id = it.id
                val nombre = it.getString("name") ?: return@mapNotNull null
                id to nombre
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Agregar a playlist", color = Color.White) },
        text = {
            if (playlists.isEmpty()) {
                Text("No tienes playlists disponibles", color = Color.Gray)
            } else {
                Column {
                    playlists.forEach { (id, nombre) ->
                        TextButton(onClick = {
                            val data = hashMapOf(
                                "id" to cancion.id,
                                "titulo" to cancion.titulo,
                                "artista" to cancion.artista
                            )

                            // Agregar canción a la playlist seleccionada
                            db.collection("usuarios").document(uid!!)
                                .collection("playlists").document(id)
                                .collection("canciones").document(cancion.id)
                                .set(data)

                            onAddToPlaylist(id, nombre)
                            onDismiss()
                        }) {
                            Text(nombre, color = Color(0xFF1DB954))
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(12.dp)
    )
}
