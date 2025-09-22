package com.example.spotifyclone.Compos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                title = { Text("Buscar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
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
                    label = { Text("Buscar canción o artista") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.buscar(query.text) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                resultados.isEmpty() -> {
                    Text("No se encontraron resultados", style = MaterialTheme.typography.bodyMedium)
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = cancion.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = cancion.artista, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
