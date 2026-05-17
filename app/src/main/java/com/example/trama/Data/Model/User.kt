package com.example.trama.Data.Model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val biography: String = "",
    val profilePicture: String = "",
    val followers: Map<String, Boolean> = emptyMap(),
    val following: Map<String, Boolean> = emptyMap(),
    val pendingRequests: Map<String, Boolean> = emptyMap()
)