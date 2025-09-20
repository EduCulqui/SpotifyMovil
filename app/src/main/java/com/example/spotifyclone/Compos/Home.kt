package com.example.spotifyclone.Compos

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.spotifyclone.viewmodels.PlaylistViewModel


/* ---------------- HOME ---------------- */
///
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlaylistClick: (String, String) -> Unit,
    viewModel: PlaylistViewModel = viewModel()
) {
    val featuredPlaylists = listOf(
        Triple("top_peru", "Top Perú", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("descubrimiento_semanal", "Descubrimiento Semanal", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("exitos_globales", "Éxitos Globales", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("rock", "Lo mejor del Rock", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("novedades_latinas", "Novedades Latinas", "https://i.imgur.com/gkScsMz.jpeg")
    )

    val recommendedPlaylists = listOf(
        Triple("dj_mix", "DJ mix", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("baladas", "Baladas", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("reggaeton_2023", "Reggaeton 2023", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("pop", "Pop", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("cumbia", "Cumbia", "https://i.imgur.com/gkScsMz.jpeg")
    )

    val morePlaylists = listOf(
        Triple("clasicos", "Clásicos", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("salsa", "Salsa", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("pachanga", "Pachanga", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("perreo", "Perreo", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("rap", "Rap", "https://i.imgur.com/gkScsMz.jpeg")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("HomeScreen", "Configuración presionada")
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Log.d("HomeScreen", "Perfil presionado")
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 👉 Botón para poblar datos de prueba
            item {
                Button(
                    onClick = { viewModel.seedData() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cargar datos de prueba en Firestore")
                }
            }

            item {
                Section(title = "Listas destacadas", playlists = featuredPlaylists, onPlaylistClick = onPlaylistClick)
            }
            item {
                Section(title = "Recomendados para ti", playlists = recommendedPlaylists, onPlaylistClick = onPlaylistClick)
            }
            item {
                Section(title = "Lo que escuchas seguido", playlists = morePlaylists, onPlaylistClick = onPlaylistClick)
            }
        }
    }
}

/* ---------------- Bottom Navigation ---------------- */
@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.Black) {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO: navegar a Home */ },
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
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: navegar a Buscar */ },
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
            selected = false,
            onClick = { /* TODO: navegar a Biblioteca */ },
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




/* ---------------- SECCIÓN REUTILIZABLE ---------------- */
@Composable
fun Section(
    title: String,
    playlists: List<Triple<String, String, String>>,
    onPlaylistClick: (String, String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(playlists) { (id, name, url) ->
                PlaylistCard(
                    name = name,
                    imageUrl = url,
                    onClick = { onPlaylistClick(id, name) }
                )
            }
        }
    }
}

/* ---------------- CARD DE PLAYLIST ---------------- */
@Composable
fun PlaylistCard(name: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}