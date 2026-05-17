package com.example.trama.Data.Model

data class Notification(
    val id: String = "",
    val type: String = "",
    val fromUid: String = "",
    val toUid: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false
)