package com.example.spotifyclone.Compos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.spotifyclone.viewmodels.AlbumViewModel

//
/* ---------------- HOME ---------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAlbumClick: (String, String) -> Unit,
    onConfigClick: () -> Unit,
    onPerfilClick: () -> Unit,
    viewModel: AlbumViewModel = viewModel()
) {
    val featuredAlbums = listOf(
        Triple("top_peru", "Top Perú", "https://i.postimg.cc/NLqfkW46/topPeru.jpg"),
        Triple("descubrimiento_semanal", "Descubrimiento Semanal", "https://i.postimg.cc/6q1C65B9/desc-Semanal.jpg"),
        Triple("exitos_globales", "Éxitos Globales", "https://i.postimg.cc/sXxXjPxh/global-Music.png"),
        Triple("rock", "Lo mejor del Rock", "https://i.imgur.com/gkScsMz.jpeg"),
        Triple("novedades_latinas", "Novedades Latinas", "https://i.postimg.cc/pXfn1ZFW/Music-Latin.jpg")
    )

    val recommendedAlbums = listOf(
        Triple("dj_mix", "DJ mix", "https://i.postimg.cc/v8LQJS3N/djMix.png"),
        Triple("baladas", "Baladas", "https://i.postimg.cc/jjXc0Tnj/balad.jpg"),
        Triple("reggaeton_2023", "Reggaeton 2023", "https://i.postimg.cc/59ZRmxBc/reggaeton.jpg"),
        Triple("pop", "Pop", "https://i.postimg.cc/765KgcbY/pop.jpg"),
        Triple("cumbia", "Cumbia", "https://i.postimg.cc/rFNNtcgN/cumbia.jpg")
    )

    val moreAlbums = listOf(
        Triple("clasicos", "Clásicos", "https://i.postimg.cc/L5zw170g/clasico.jpg"),
        Triple("salsa", "Salsa", "https://i.postimg.cc/cJgD32v1/salsa.jpg"),
        Triple("pachanga", "Pachanga", "https://i.postimg.cc/QNkfcpd7/pachanga.jpg"),
        Triple("perreo", "Perreo", "https://i.postimg.cc/TwTDxWv8/perreo.jpg"),
        Triple("rap", "Rap", "https://i.postimg.cc/qB5zbFqH/rap.jpg")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onConfigClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) // fondo oscuro tipo Spotify
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Section(
                    title = "Álbumes destacados",
                    albums = featuredAlbums,
                    onAlbumClick = onAlbumClick
                )
            }
            item {
                Section(
                    title = "Recomendados para ti",
                    albums = recommendedAlbums,
                    onAlbumClick = onAlbumClick
                )
            }
            item {
                Section(
                    title = "Lo que escuchas seguido",
                    albums = moreAlbums,
                    onAlbumClick = onAlbumClick
                )
            }
        }
    }
}

/* ---------------- SECCIÓN REUTILIZABLE ---------------- */
@Composable
fun Section(
    title: String,
    albums: List<Triple<String, String, String>>,
    onAlbumClick: (String, String) -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(albums) { (id, name, url) ->
                AlbumCard(
                    name = name,
                    imageUrl = url,
                    onClick = { onAlbumClick(id, name) }
                )
            }
        }
    }
}

/* ---------------- CARD DE ÁLBUM ---------------- */
@Composable
fun AlbumCard(name: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // card oscuro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1DB954).copy(alpha = 0.2f), Color(0xFF121212))
                    )
                ),
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
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }
    }
}