package com.example.spotifyclone.Compos

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.tuapp.spotifyclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarUsuariosScreen(
    miId: String,
    onBack: () -> Unit,
    onVerPlaylists: (String) -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val usuarios by viewModel.usuarios.collectAsState()
    val miUsuario by viewModel.usuarioActual.collectAsState() // ✅ nombre correcto
    val scope = rememberCoroutineScope()

    // Cargar el usuario actual (para saber a quién sigue)
    LaunchedEffect(miId) {
        if (miId.isNotEmpty()) viewModel.cargarUsuarioActual(miId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar usuarios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (it.length >= 2) viewModel.buscarUsuarios(it.trim()) // ✅ asegura que existe en ViewModel
                },
                label = { Text("Buscar por nombre, apellido o email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(usuarios) { usuario ->
                    if (usuario.id != null && usuario.id != miId) {
                        val siguiendo = miUsuario?.siguiendo?.contains(usuario.id) == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(usuario.fotoPerfil ?: ""),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable { usuario.id?.let { onVerPlaylists(it) } }
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${usuario.nombre} ${usuario.apellido}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.clickable { usuario.id?.let { onVerPlaylists(it) } }
                                )
                                Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        if (siguiendo)
                                            viewModel.dejarDeSeguirUsuario(miId, usuario.id!!) // ✅ nombre corregido
                                        else
                                            viewModel.seguirUsuario(miId, usuario.id!!)
                                    }
                                }
                            ) { Text(if (siguiendo) "Siguiendo" else "Seguir") }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}
