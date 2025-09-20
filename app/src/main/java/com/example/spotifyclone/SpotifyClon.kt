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
import com.example.spotifyclone.Compos.PlaylistScreen

@OptIn(ExperimentalMaterial3Api::class)
class SpotifyClon : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SpotifyCloneApp()
            }
        }
    }
}

private enum class Screen { Login, Register, Home, Playlist }

@Composable
private fun SpotifyCloneApp() {
    var current by rememberSaveable { mutableStateOf(Screen.Login) }

    // Guardamos la playlist seleccionada
    var selectedPlaylistId by rememberSaveable { mutableStateOf("") }
    var selectedPlaylistName by rememberSaveable { mutableStateOf("") }

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
            }
        )
        Screen.Playlist -> PlaylistScreen(
            playlistId = selectedPlaylistId,
            playlistName = selectedPlaylistName,
            onBack = { current = Screen.Home }
        )
    }
}
