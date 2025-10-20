package com.example.spotifyclone.repository

import com.example.spotifyclone.entities.Cancion
import com.example.spotifyclone.entities.Playlist
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
        if (idActual == idOtro) return // Evita seguirse a sí mismo
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
    // 🔹 Obtener todas las playlists de un usuario (estructura anidada en usuarios/{uid}/playlists)
    // 🔹 Obtener todas las playlists de un usuario
    suspend fun obtenerPlaylistsDeUsuario(userId: String): List<Playlist> {
        return try {
            val snapshot = db.collection("usuarios")
                .document(userId)
                .collection("playlists")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: "Sin nombre"
                Playlist(
                    id = doc.id,
                    name = name
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    // 🔹 Obtener canciones de una playlist de un usuario
    suspend fun obtenerCancionesDePlaylist(userId: String, playlistId: String): List<Cancion> {
        return try {
            val snapshot = db.collection("usuarios")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .collection("canciones")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cancion::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }




}
