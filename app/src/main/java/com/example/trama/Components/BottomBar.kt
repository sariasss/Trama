package com.example.trama.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trama.Screen.AppDestinations

//footer
@Composable
fun BottomBar(navController: NavHostController) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = Color(0xFF1C1A1C)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                navController.navigate(AppDestinations.Inicio.route) {
                    popUpTo(AppDestinations.Inicio.route) { saveState = true }
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = if (currentRoute == AppDestinations.Inicio.route)
                        Color(0xFF760B45)
                    else Color.White
                )
            }

            IconButton(onClick = {
                navController.navigate(AppDestinations.Buscador.route) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = if (currentRoute == AppDestinations.Buscador.route)
                        Color(0xFF760B45)
                    else Color.White
                )
            }

            IconButton(onClick = {
                navController.navigate(AppDestinations.Perfil.route) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = if (currentRoute == AppDestinations.Perfil.route)
                        Color(0xFF760B45)
                    else Color.White
                )
            }
        }
    }
}