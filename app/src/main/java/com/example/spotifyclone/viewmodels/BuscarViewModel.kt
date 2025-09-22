package com.example.spotifyclone.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.entities.Cancion
import com.google.firebase.firestore.FirebaseFirestore

class BuscarViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var resultados = mutableStateOf<List<Cancion>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun buscar(query: String) {
        if (query.isBlank()) {
            resultados.value = emptyList()
            return
        }

        isLoading.value = true

        // 🔎 Buscar en todas las colecciones "canciones"
        db.collectionGroup("canciones")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val cancion = doc.toObject(Cancion::class.java)
                    cancion?.copy(id = doc.id)
                }.filter {
                    it.titulo.contains(query, ignoreCase = true) ||
                            it.artista.contains(query, ignoreCase = true)
                }
                resultados.value = lista
                isLoading.value = false
            }
            .addOnFailureListener {
                resultados.value = emptyList()
                isLoading.value = false
            }
    }
}
