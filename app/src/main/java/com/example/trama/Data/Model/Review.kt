package com.example.trama.Data.Model

data class Review(
    val id: String = "",
    val movieId: Int = 0,       // Relación con la película de TMDb
    val movieTitle: String = "",
    val userId: String = "",    // Relación con el usuario que la escribe
    val username: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = 0L
)