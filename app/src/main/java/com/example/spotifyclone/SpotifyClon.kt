package com.example.spotifyclone
import com.example.spotifyclone.Compos.HomeScreen
import com.example.spotifyclone.Compos.LoginScreen
import com.example.spotifyclone.Compos.RegisterScreen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.Compos.AlbumScreen
import com.example.spotifyclone.Compos.BuscarScreen
import com.example.spotifyclone.Compos.BuscarUsuariosScreen
import com.example.spotifyclone.Compos.ConfigScreen
import com.example.spotifyclone.Compos.LibraryScreen
import com.example.spotifyclone.Compos.ListaUsuariosScreen
import com.example.spotifyclone.Compos.PerfilScreen
import com.example.spotifyclone.Compos.PlaylistDetalleScreen
import com.example.spotifyclone.Compos.PlaylistScreen
import com.example.spotifyclone.Compos.PlaylistsUsuarioScreen
import com.example.spotifyclone.repository.UserRepository
import com.example.spotifyclone.viewmodels.AlbumViewModel
import com.example.spotifyclone.viewmodels.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.spotifyclone.viewmodel.UserViewModel

//
@OptIn(ExperimentalMaterial3Api::class)
class SpotifyClon : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val isLoggedIn = auth.currentUser != null  // Verifica si hay usuario activo

        setContent {
            MaterialTheme {
                SpotifyCloneApp(isLoggedIn)
            }
        }
    }
}

private enum class Screen { Login, Register, Home, Album, Buscar, Config, Perfil, Biblioteca, Playlist,BuscarUsuarios, PlaylistsUsuario, ListaSeguidores, ListaSiguiendo, PlaylistDetalle  }


@Composable
private fun SpotifyCloneApp(isLoggedIn: Boolean) {
    // 🔹 Usar FirebaseAuth en vez de solo SharedPreferences
    var current by rememberSaveable {
        mutableStateOf(if (isLoggedIn) Screen.Home else Screen.Login)
    }

    var selectedAlbumId by rememberSaveable { mutableStateOf("") }
    var selectedAlbumName by rememberSaveable { mutableStateOf("") }
    val albumVm: AlbumViewModel = viewModel()

    val selectedPlaylistId = remember { mutableStateOf("") }
    val selectedPlaylistName = remember { mutableStateOf("") }
    val selectedPlaylistOwnerId = remember { mutableStateOf("") }

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(UserRepository())
    )
    val miUsuario by userViewModel.usuarioActual.collectAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val miId = FirebaseAuth.getInstance().currentUser?.uid
        if (miId != null) {
            userViewModel.cargarUsuarioActual(miId)
        }
    }



    LaunchedEffect(Unit) {
        albumVm.seedData()
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
                    onAlbumClick = { id, name ->
                        selectedAlbumId = id
                        selectedAlbumName = name
                        current = Screen.Album
                    },
                    onConfigClick = { current = Screen.Config },
                    onPerfilClick = { current = Screen.Perfil }
                )

                Screen.Album -> AlbumScreen(
                    albumId = selectedAlbumId,
                    albumName = selectedAlbumName,
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
                    onBack = { current = Screen.Config },
                    onBuscarUsuarios = { current = Screen.BuscarUsuarios },
                    onVerSeguidores = {
                        if (miUsuario?.seguidores?.isNotEmpty() == true) {
                            current = Screen.ListaSeguidores
                        } else {
                            Toast.makeText(context, "No tienes seguidores aún", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onVerSiguiendo = {
                        if (miUsuario?.siguiendo?.isNotEmpty() == true) {
                            current = Screen.ListaSiguiendo
                        } else {
                            Toast.makeText(context, "No sigues a nadie aún", Toast.LENGTH_SHORT).show()
                        }
                    }

                )
                Screen.Biblioteca -> LibraryScreen(
                    onBack = { current = Screen.Home },
                    onOpenPlaylist = { id, name ->
                        // usar .value para actualizar
                        selectedPlaylistId.value = id
                        selectedPlaylistName.value = name
                        current = Screen.Playlist
                    }
                )

                Screen.Playlist -> PlaylistScreen(
                    uid = FirebaseAuth.getInstance().currentUser?.uid ?: "", // uid actual
                    playlistId = selectedPlaylistId.value,                  // tu id de playlist
                    name = selectedPlaylistName.value,
                    onBack = { current = Screen.Biblioteca }
                )

                Screen.BuscarUsuarios -> BuscarUsuariosScreen(
                    miId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    onBack = { current = Screen.Perfil }, // 🔹 agrega esto para volver a la pantalla anterior
                    onVerPlaylists = { userId ->
                        selectedPlaylistOwnerId.value = userId
                        current = Screen.PlaylistsUsuario
                    }
                )

                Screen.PlaylistsUsuario -> PlaylistsUsuarioScreen(
                    userId = selectedPlaylistOwnerId.value,
                    onBack = { current = Screen.BuscarUsuarios },
                    onAbrirPlaylist = { playlistId, nombre ->
                        selectedPlaylistId.value = playlistId
                        selectedPlaylistName.value = nombre
                        current = Screen.PlaylistDetalle // 👈 ABRE LA NUEVA PANTALLA
                    }
                )
                Screen.PlaylistDetalle -> PlaylistDetalleScreen(
                    userId = selectedPlaylistOwnerId.value,
                    playlistId = selectedPlaylistId.value,
                    playlistNombre = selectedPlaylistName.value,
                    onBack = { current = Screen.PlaylistsUsuario }
                )


                Screen.Playlist -> PlaylistScreen(
                    uid = selectedPlaylistOwnerId.value.ifEmpty {
                        FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    },
                    playlistId = selectedPlaylistId.value,
                    name = selectedPlaylistName.value,
                    onBack = {
                        current = if (selectedPlaylistOwnerId.value.isEmpty())
                            Screen.Biblioteca
                        else
                            Screen.PlaylistsUsuario
                    }
                )
                Screen.PlaylistsUsuario -> PlaylistsUsuarioScreen(
                    userId = selectedPlaylistOwnerId.value,
                    onBack = { current = Screen.BuscarUsuarios },
                    onAbrirPlaylist = { playlistId, nombre ->
                        selectedPlaylistId.value = playlistId
                        selectedPlaylistName.value = nombre
                        current = Screen.Playlist
                    }
                )

                // ✅ Solo este PlaylistScreen queda, elimina el anterior duplicado
                Screen.Playlist -> PlaylistScreen(
                    uid = selectedPlaylistOwnerId.value.ifEmpty {
                        FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    },
                    playlistId = selectedPlaylistId.value,
                    name = selectedPlaylistName.value,
                    onBack = {
                        current = if (selectedPlaylistOwnerId.value.isEmpty())
                            Screen.Biblioteca
                        else
                            Screen.PlaylistsUsuario
                    }
                )

                Screen.ListaSeguidores -> {
                    if (miUsuario != null && miUsuario?.seguidores != null) {
                        ListaUsuariosScreen(
                            userIds = miUsuario?.seguidores ?: emptyList(),
                            titulo = "Seguidores",
                            onBack = { current = Screen.Perfil },
                            onUsuarioClick = { userId ->
                                selectedPlaylistOwnerId.value = userId
                                current = Screen.PlaylistsUsuario
                            },
                            viewModel = userViewModel
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1DB954))
                        }
                    }
                }

                Screen.ListaSiguiendo -> {
                    if (miUsuario != null && miUsuario?.siguiendo != null) {
                        ListaUsuariosScreen(
                            userIds = miUsuario?.siguiendo ?: emptyList(),
                            titulo = "Siguiendo",
                            onBack = { current = Screen.Perfil },
                            onUsuarioClick = { userId ->
                                selectedPlaylistOwnerId.value = userId
                                current = Screen.PlaylistsUsuario
                            },
                            viewModel = userViewModel
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1DB954))
                        }
                    }
                }





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


        NavigationBarItem(
            selected = current == Screen.Biblioteca,
            onClick = { onNavigate(Screen.Biblioteca) },
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
