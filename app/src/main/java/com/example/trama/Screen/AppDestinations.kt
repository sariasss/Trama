package com.example.trama.Screen

import android.net.Uri
sealed class AppDestinations(val route: String) {
    object Inicio : AppDestinations("inicio")
    object Buscador : AppDestinations("buscador")
    object Perfil : AppDestinations("perfil")
    object PerfilUsuario : AppDestinations("usuario/{userId}") {
        fun createRoute(userId: String) = "usuario/$userId"
    }
    object Detalle : AppDestinations("detalle/{title}") {
        fun createRoute(title: String) = "detalle/${Uri.encode(title)}"
    }
}