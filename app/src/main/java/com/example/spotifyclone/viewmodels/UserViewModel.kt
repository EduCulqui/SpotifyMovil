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

    // --- NUEVOS flujos reactivos para seguidores y siguiendo ---
    private val _seguidoresUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val seguidoresUsuarios: StateFlow<List<Usuario>> = _seguidoresUsuarios

    private val _siguiendoUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val siguiendoUsuarios: StateFlow<List<Usuario>> = _siguiendoUsuarios

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
    // (se mantiene como suspend para uso puntual)
    suspend fun obtenerUsuariosPorIds(listaIds: List<String>): List<Usuario> {
        if (listaIds.isEmpty()) return emptyList()
        return try {
            repository.obtenerUsuariosPorIds(listaIds)
        } catch (e: Exception) {
            _error.value = e.message
            emptyList()
        }
    }

    // 🔹 Cargar usuario actual (y además cargar sus seguidores/siguiendo reactivos)
    fun cargarUsuarioActual(id: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val user = repository.obtenerUsuarioPorId(id)
                val usuarioLimpio = user.copy(
                    seguidores = user.seguidores ?: emptyList(),
                    siguiendo = user.siguiendo ?: emptyList()
                )
                _usuarioActual.value = usuarioLimpio

                // Cargar las listas de usuarios (detalles) de seguidores y siguiendo
                // Si las listas son grandes, esto puede dividirse/optimizarse.
                _siguiendoUsuarios.value = if (usuarioLimpio.siguiendo.isNullOrEmpty()) {
                    emptyList()
                } else {
                    repository.obtenerUsuariosPorIds(usuarioLimpio.siguiendo)
                }

                _seguidoresUsuarios.value = if (usuarioLimpio.seguidores.isNullOrEmpty()) {
                    emptyList()
                } else {
                    repository.obtenerUsuariosPorIds(usuarioLimpio.seguidores)
                }

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
                // actualizar el estado local optimista
                actualizarEstadoLocal(idOtro, seguir = true)

                // además actualizar las colecciones de objetos (detalles) de siguiendo/seguidores
                // Intentamos añadir el objeto Usuario desde la lista general si existe
                val posible = _usuarios.value.find { it.id == idOtro }
                posible?.let { seguidoUsuario ->
                    // actualizar lista de siguiendo del usuario actual
                    _siguiendoUsuarios.value = _siguiendoUsuarios.value + seguidoUsuario
                }
                // actualizar seguidores del usuario "otro" si lo tenemos en _usuarios
                val idActualNoNulo = _usuarioActual.value?.id
                if (idActualNoNulo != null) {
                    _usuarios.value = _usuarios.value.map { usuario ->
                        if (usuario.id == idOtro) {
                            val nuevosSeguidores = (usuario.seguidores ?: emptyList()).toMutableList()
                            if (!nuevosSeguidores.contains(idActualNoNulo)) nuevosSeguidores.add(idActualNoNulo)
                            usuario.copy(seguidores = nuevosSeguidores)
                        } else usuario
                    }

                    // Si estamos viendo el perfil del otro usuario en memoria (por ejemplo en _seguidoresUsuarios),
                    // actualizamos esa lista para que la UI también lo vea.
                    _seguidoresUsuarios.value = _seguidoresUsuarios.value.map { u ->
                        if (u.id == idOtro) {
                            val nuevos = (u.seguidores ?: emptyList()).toMutableList()
                            if (!nuevos.contains(idActualNoNulo)) nuevos.add(idActualNoNulo)
                            u.copy(seguidores = nuevos)
                        } else u
                    }
                }

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
                // actualizar el estado local
                actualizarEstadoLocal(idOtro, seguir = false)

                // actualizar listas de objetos (detalles)
                _siguiendoUsuarios.value = _siguiendoUsuarios.value.filterNot { it.id == idOtro }

                val idActualNoNulo = _usuarioActual.value?.id
                if (idActualNoNulo != null) {
                    _usuarios.value = _usuarios.value.map { usuario ->
                        if (usuario.id == idOtro) {
                            val nuevosSeguidores = (usuario.seguidores ?: emptyList()).toMutableList()
                            nuevosSeguidores.remove(idActualNoNulo)
                            usuario.copy(seguidores = nuevosSeguidores)
                        } else usuario
                    }

                    _seguidoresUsuarios.value = _seguidoresUsuarios.value.map { u ->
                        if (u.id == idOtro) {
                            val nuevos = (u.seguidores ?: emptyList()).toMutableList()
                            nuevos.remove(idActualNoNulo)
                            u.copy(seguidores = nuevos)
                        } else u
                    }
                }

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

        // Actualiza usuarioActual
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
