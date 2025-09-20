package com.example.spotifyclone.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.entities.Cancion
import com.google.firebase.firestore.FirebaseFirestore

//
class PlaylistViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var canciones = mutableStateOf<List<Cancion>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun cargarCanciones(playlistId: String) {
        isLoading.value = true

        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val cancion = doc.toObject(Cancion::class.java)
                    cancion?.copy(id = doc.id)
                }
                canciones.value = lista
                isLoading.value = false
            }
            .addOnFailureListener {
                canciones.value = emptyList()
                isLoading.value = false
            }
    }

    // 👉 Sembrar datos de prueba
    fun seedData() {
        val playlists = listOf(
            Triple("top_peru", "Top Perú", "https://i.imgur.com/gkScsMz.jpeg"),
            Triple("exitos_globales", "Éxitos Globales", "https://i.imgur.com/gkScsMz.jpeg")
        )

        playlists.forEach { (id, name, imageUrl) ->
            val playlistRef = db.collection("playlists").document(id)

            // Datos básicos de la playlist
            playlistRef.set(
                mapOf(
                    "name" to name,
                    "imageUrl" to imageUrl
                )
            ).addOnSuccessListener {
                // Canciones de prueba
                val canciones = listOf(
                    Cancion(titulo = "Canción 1 de $name", artista = "Artista A"),
                    Cancion(titulo = "Canción 2 de $name", artista = "Artista B"),
                    Cancion(titulo = "Canción 3 de $name", artista = "Artista C")
                )

                canciones.forEach { song ->
                    playlistRef.collection("songs").add(song)
                }
            }
        }
    }

    // 👉 Agregar una canción manualmente a una playlist existente
    fun agregarCancion(playlistId: String, cancion: Cancion) {
        db.collection("playlists")
            .document(playlistId)
            .collection("songs")
            .add(cancion)
    }
}
