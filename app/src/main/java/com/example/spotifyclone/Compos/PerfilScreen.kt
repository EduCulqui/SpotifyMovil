package com.example.spotifyclone.Compos

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.spotifyclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit,
    onBuscarUsuarios: () -> Unit,
    onVerSeguidores: () -> Unit,
    onVerSiguiendo: () -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    // ✅ nombre correcto: usuarioActual
    val miUsuario = viewModel.usuarioActual.collectAsState(initial = null).value

    val uid = auth.currentUser?.uid

    // ✅ método correcto: cargarUsuarioActual
    LaunchedEffect(uid) {
        if (uid != null) {
            viewModel.cargarUsuarioActual(uid)
        } else {
            Toast.makeText(ctx, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1DB954)
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (miUsuario == null) {
                CircularProgressIndicator(color = Color(0xFF1DB954))
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF121212), Color(0xFF1A1A1A))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp)
                ) {
                    // 🔹 Foto de perfil
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF1DB954).copy(alpha = 0.4f), Color.Transparent)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = miUsuario?.fotoPerfil ?: ""),
                            contentDescription = "Foto perfil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }

                    // 🔹 Datos del usuario
                    Text(
                        text = "${miUsuario?.nombre ?: ""} ${miUsuario?.apellido ?: ""}".trim(),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                    Text(
                        text = miUsuario?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🔹 Contadores de seguidores / siguiendo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onVerSeguidores() }
                        ) {
                            Text(
                                text = "${miUsuario?.seguidores?.size ?: 0}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Seguidores", color = Color.Gray)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onVerSiguiendo() }
                        ) {
                            Text(
                                text = "${miUsuario?.siguiendo?.size ?: 0}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Siguiendo", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 🔹 Botón para buscar otros usuarios
                    Button(
                        onClick = onBuscarUsuarios,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                    ) {
                        Icon(Icons.Default.PersonSearch, contentDescription = "Buscar usuarios")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar otros usuarios")
                    }
                }
            }
        }
    }
}
