package com.example.spotifyclone.Compos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.offline.Download
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onGoPerfil: () -> Unit // 👉 navegación al perfil
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Opciones", style = MaterialTheme.typography.titleMedium)

            // 🔹 Nueva opción de Perfil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoPerfil() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Perfil")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: cambiar idioma */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Language, contentDescription = "Idioma")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Idioma")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: notificaciones */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Notificaciones")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: descargas */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Download, contentDescription = "Descargas")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Descargas")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: ayuda */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Help, contentDescription = "Ayuda")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Ayuda")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: acerca de */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = "Acerca de")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Acerca de")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: cambiar tema */ }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Brightness6, contentDescription = "Tema")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cambiar tema")
            }

            // 🔹 Cerrar sesión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cerrar sesión")
            }
        }
    }
}
