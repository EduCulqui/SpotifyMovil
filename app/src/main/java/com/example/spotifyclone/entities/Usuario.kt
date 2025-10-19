package com.example.spotifyclone.entities

data class Usuario(
    val id: String? = null,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val fotoPerfil: String? = null,
    val seguidores: List<String> = emptyList(),
    val siguiendo: List<String> = emptyList()
)