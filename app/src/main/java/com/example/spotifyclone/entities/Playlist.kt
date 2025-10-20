package com.example.spotifyclone.entities

data class Playlist(
    val id: String = "",
    val name: String = "",
    val descripcion: String = "",
    val canciones: List<String> = emptyList()
)