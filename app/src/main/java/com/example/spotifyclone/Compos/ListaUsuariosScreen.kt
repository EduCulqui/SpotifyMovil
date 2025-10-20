package com.example.spotifyclone.Compos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyclone.entities.Usuario
import com.tuapp.spotifyclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

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

    // 🎨 Fondo tipo Spotify (degradado oscuro a verde)
    val fondo = Brush.verticalGradient(
        colors = listOf(Color(0xFF121212), Color(0xFF1DB954))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        titulo,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondo),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(usuarios) { usuario ->
                usuario.id?.let { uid ->

                    // 💫 Animación de escala suave al presionar
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.96f else 1f,
                        animationSpec = tween(durationMillis = 150)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(
                                onClickLabel = "Ver perfil de ${usuario.nombre}"
                            ) {
                                isPressed = true
                                onUsuarioClick(uid)
                                // 🔁 Efecto rebote después del toque
                                isPressed = false
                            }
                            .background(Color(0xFF1E1E1E).copy(alpha = 0.8f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(usuario.fotoPerfil ?: ""),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.DarkGray)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                "${usuario.nombre} ${usuario.apellido}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                usuario.email,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                            )
                        }
                    }
                }
            }
        }
    }
}
