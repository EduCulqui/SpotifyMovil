package com.example.spotifyclone.Compos

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyclone.entities.Usuario
import com.tuapp.spotifyclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuariosScreen(
    userIds: List<String>,
    titulo: String,
    onBack: () -> Unit,
    onUsuarioClick: (String) -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userIds) {
        scope.launch { usuarios = viewModel.obtenerUsuariosPorIds(userIds) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(usuarios) { usuario ->
                usuario.id?.let { uid ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onUsuarioClick(uid) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(usuario.fotoPerfil ?: ""),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("${usuario.nombre} ${usuario.apellido}", style = MaterialTheme.typography.titleMedium)
                            Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

