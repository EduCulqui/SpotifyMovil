package com.tuapp.spotifyclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.entities.Usuario
import com.example.spotifyclone.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 🔹 Cargar todos los usuarios (excepto el actual)
    fun cargarUsuarios(idActual: String?) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val lista = repository.obtenerTodosLosUsuarios()
                _usuarios.value = if (idActual != null) {
                    lista.filter { it.id != idActual }
                } else lista
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _cargando.value = false
            }
        }
    }

    // 🔹 Obtener usuarios por una lista de IDs (seguidores o siguiendo)
    suspend fun obtenerUsuariosPorIds(listaIds: List<String>): List<Usuario> {
        if (listaIds.isEmpty()) return emptyList()
        return try {
            repository.obtenerUsuariosPorIds(listaIds)
        } catch (e: Exception) {
            _error.value = e.message
            emptyList()
        }
    }

    // 🔹 Cargar usuario actual
    fun cargarUsuarioActual(id: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val user = repository.obtenerUsuarioPorId(id)
                _usuarioActual.value = user.copy(
                    seguidores = user.seguidores ?: emptyList(),
                    siguiendo = user.siguiendo ?: emptyList()
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _cargando.value = false
            }
        }
    }

    // 🔹 Buscar usuarios por nombre, apellido o email
    fun buscarUsuarios(query: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val todos = repository.obtenerTodosLosUsuarios()
                _usuarios.value = todos.filter { usuario ->
                    listOfNotNull(
                        usuario.nombre,
                        usuario.apellido,
                        usuario.email
                    ).any { campo ->
                        campo.contains(query, ignoreCase = true)
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _cargando.value = false
            }
        }
    }


    // 🔹 Seguir a otro usuario
    fun seguirUsuario(idActual: String, idOtro: String) {
        viewModelScope.launch {
            try {
                repository.seguirUsuario(idActual, idOtro)
                actualizarEstadoLocal(idOtro, seguir = true)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // 🔹 Dejar de seguir
    fun dejarDeSeguirUsuario(idActual: String, idOtro: String) {
        viewModelScope.launch {
            try {
                repository.dejarDeSeguirUsuario(idActual, idOtro)
                actualizarEstadoLocal(idOtro, seguir = false)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // 🔹 Actualiza el estado local (sin recargar desde Firestore)
    private fun actualizarEstadoLocal(idOtro: String, seguir: Boolean) {
        val actual = _usuarioActual.value ?: return
        val nuevosSiguiendo = (actual.siguiendo ?: emptyList()).toMutableList()

        if (seguir) {
            if (!nuevosSiguiendo.contains(idOtro)) nuevosSiguiendo.add(idOtro)
        } else {
            nuevosSiguiendo.remove(idOtro)
        }

        _usuarioActual.value = actual.copy(siguiendo = nuevosSiguiendo)

        // 🔹 Actualizar lista general de usuarios para que Compose reaccione
        val idActualNoNulo = actual.id ?: return
        _usuarios.value = _usuarios.value.map { usuario ->
            if (usuario.id == idOtro) {
                val nuevosSeguidores = (usuario.seguidores ?: emptyList()).toMutableList()
                if (seguir) {
                    if (!nuevosSeguidores.contains(idActualNoNulo)) nuevosSeguidores.add(idActualNoNulo)
                } else {
                    nuevosSeguidores.remove(idActualNoNulo)
                }
                usuario.copy(seguidores = nuevosSeguidores)
            } else usuario
        }
    }
}
