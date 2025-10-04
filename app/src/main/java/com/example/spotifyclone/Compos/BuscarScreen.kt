package com.example.spotifyclone.Compos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.entities.Cancion
import com.example.spotifyclone.viewmodels.BuscarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(
    onBack: () -> Unit,
    viewModel: BuscarViewModel = viewModel()
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    val resultados by viewModel.resultados
    val isLoading by viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFF121212) // Fondo oscuro tipo Spotify
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Canción o artista") },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1DB954),
                        unfocusedBorderColor = Color.DarkGray,
                        disabledBorderColor = Color.DarkGray,
                        cursorColor = Color(0xFF1DB954),
                        focusedLabelColor = Color(0xFF1DB954),
                        unfocusedLabelColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        selectionColors = TextSelectionColors(
                            handleColor = Color(0xFF1DB954),
                            backgroundColor = Color(0xFF1DB954).copy(alpha = 0.3f)
                        )
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.buscar(query.text) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF1DB954)
                    )
                }
                resultados.isEmpty() -> {
                    Text(
                        "No se encontraron resultados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(resultados) { cancion ->
                            ResultadoItem(cancion)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultadoItem(cancion: Cancion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)), // sombra sutil
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = cancion.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = cancion.artista,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
