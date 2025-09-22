package com.example.spotifyclone
import com.example.spotifyclone.Compos.HomeScreen
import com.example.spotifyclone.Compos.LoginScreen
import com.example.spotifyclone.Compos.RegisterScreen

import coil.compose.AsyncImage
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.runtime.Config
import com.example.spotifyclone.Compos.BuscarScreen
import com.example.spotifyclone.Compos.ConfigScreen
import com.example.spotifyclone.Compos.PerfilScreen
import com.example.spotifyclone.Compos.PlaylistScreen
import com.example.spotifyclone.Compos.PreferencesManager
import com.example.spotifyclone.viewmodels.PlaylistViewModel
import com.google.firebase.auth.FirebaseAuth

//
@OptIn(ExperimentalMaterial3Api::class)
class SpotifyClon : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val isLoggedIn = auth.currentUser != null  // 🔹 Verifica si hay usuario activo

        setContent {
            MaterialTheme {
                SpotifyCloneApp(isLoggedIn)
            }
        }
    }
}

private enum class Screen { Login, Register, Home, Playlist, Buscar, Config, Perfil }

@Composable
private fun SpotifyCloneApp(isLoggedIn: Boolean) {
    // 🔹 Usar FirebaseAuth en vez de solo SharedPreferences
    var current by rememberSaveable {
        mutableStateOf(if (isLoggedIn) Screen.Home else Screen.Login)
    }

    var selectedPlaylistId by rememberSaveable { mutableStateOf("") }
    var selectedPlaylistName by rememberSaveable { mutableStateOf("") }
    val playlistVm: PlaylistViewModel = viewModel()

    LaunchedEffect(Unit) {
        playlistVm.seedData()
    }

    Scaffold(
        bottomBar = {
            if (current == Screen.Home || current == Screen.Buscar) {
                BottomNavigationBar(
                    current = current,
                    onNavigate = { current = it }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (current) {
                Screen.Login -> LoginScreen(
                    onLogin = { current = Screen.Home },
                    onGoRegister = { current = Screen.Register }
                )
                Screen.Register -> RegisterScreen(
                    onRegistered = { current = Screen.Login }
                )
                Screen.Home -> HomeScreen(
                    onPlaylistClick = { id, name ->
                        selectedPlaylistId = id
                        selectedPlaylistName = name
                        current = Screen.Playlist
                    },
                    onConfigClick = { current = Screen.Config },
                    onPerfilClick = { current = Screen.Perfil }
                )
                Screen.Playlist -> PlaylistScreen(
                    playlistId = selectedPlaylistId,
                    playlistName = selectedPlaylistName,
                    onBack = { current = Screen.Home }
                )
                Screen.Buscar -> BuscarScreen(
                    onBack = { current = Screen.Home }
                )
                Screen.Config -> ConfigScreen(
                    onBack = { current = Screen.Home },
                    onLogout = { current = Screen.Login },
                    onGoPerfil = { current = Screen.Perfil }
                )

                Screen.Perfil -> PerfilScreen(
                    onBack = { current = Screen.Config }
                )
            }
        }
    }
}



/* ---------------- Bottom Navigation ---------------- */
@Composable
private fun BottomNavigationBar(
    current: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(containerColor = Color.Black) {
        // 👉 Inicio
        NavigationBarItem(
            selected = current == Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )

        // 👉 Buscar
        NavigationBarItem(
            selected = current == Screen.Buscar,
            onClick = { onNavigate(Screen.Buscar) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            label = { Text("Buscar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )

        // 👉 Biblioteca (por ahora solo decorativo)
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: implementar biblioteca */ },
            icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = "Biblioteca") },
            label = { Text("Biblioteca") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
    }
}
