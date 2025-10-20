package com.example.spotifyclone.repository

import com.example.spotifyclone.entities.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosRef = db.collection("usuarios")

    // 🔹 Obtener todos los usuarios
    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        return try {
            val snapshot = usuariosRef.get().await()
            snapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(Usuario::class.java)
                user?.copy(
                    id = doc.id,
                    seguidores = user?.seguidores ?: emptyList(),
                    siguiendo = user?.siguiendo ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Obtener un usuario por su ID
    suspend fun obtenerUsuarioPorId(id: String): Usuario {
        val doc = usuariosRef.document(id).get().await()
        val user = doc.toObject(Usuario::class.java)
        return user?.copy(
            id = doc.id,
            seguidores = user.seguidores ?: emptyList(),
            siguiendo = user.siguiendo ?: emptyList()
        ) ?: Usuario(id = id, nombre = "Desconocido", seguidores = emptyList(), siguiendo = emptyList())
    }

    // 🔹 Obtener múltiples usuarios por una lista de IDs
    suspend fun obtenerUsuariosPorIds(ids: List<String>): List<Usuario> {
        if (ids.isEmpty()) return emptyList()
        return try {
            ids.mapNotNull { id ->
                val doc = usuariosRef.document(id).get().await()
                val user = doc.toObject(Usuario::class.java)
                user?.copy(
                    id = doc.id,
                    seguidores = user.seguidores ?: emptyList(),
                    siguiendo = user.siguiendo ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    // 🔹 Seguir a un usuario
    suspend fun seguirUsuario(idActual: String, idOtro: String) {
        val usuarioActualRef = usuariosRef.document(idActual)
        val otroUsuarioRef = usuariosRef.document(idOtro)

        db.runTransaction { transaction ->
            val docActual = transaction.get(usuarioActualRef)
            val docOtro = transaction.get(otroUsuarioRef)

            // Crear campos si no existen
            val siguiendo = (docActual.get("siguiendo") as? List<String>)?.toMutableList() ?: mutableListOf()
            val seguidores = (docOtro.get("seguidores") as? List<String>)?.toMutableList() ?: mutableListOf()

            if (!siguiendo.contains(idOtro)) siguiendo.add(idOtro)
            if (!seguidores.contains(idActual)) seguidores.add(idActual)

            transaction.update(usuarioActualRef, "siguiendo", siguiendo)
            transaction.update(otroUsuarioRef, "seguidores", seguidores)
        }.await()
    }

    // 🔹 Dejar de seguir a un usuario
    suspend fun dejarDeSeguirUsuario(idActual: String, idOtro: String) {
        val usuarioActualRef = usuariosRef.document(idActual)
        val otroUsuarioRef = usuariosRef.document(idOtro)

        db.runTransaction { transaction ->
            val docActual = transaction.get(usuarioActualRef)
            val docOtro = transaction.get(otroUsuarioRef)

            val siguiendo = (docActual.get("siguiendo") as? List<String>)?.toMutableList() ?: mutableListOf()
            val seguidores = (docOtro.get("seguidores") as? List<String>)?.toMutableList() ?: mutableListOf()

            siguiendo.remove(idOtro)
            seguidores.remove(idActual)

            transaction.update(usuarioActualRef, "siguiendo", siguiendo)
            transaction.update(otroUsuarioRef, "seguidores", seguidores)
        }.await()
    }

    // 🔹 Obtener todas las playlists de un usuario
    suspend fun obtenerPlaylistsDeUsuario(userId: String): List<Map<String, Any>> {
        return try {
            val snapshot = db.collection("playlists")
                .whereEqualTo("usuarioId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                data + ("id" to doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}
