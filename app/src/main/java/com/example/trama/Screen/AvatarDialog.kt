package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// dialog cambiar avatar con opciones
@Composable
fun AvatarDialog(
    avatarActual: String,
    onSeleccionar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // avatares disponibles
    val avatares = listOf(
        "https://nupec.com/wp-content/uploads/2021/12/domestic-cat-EABDSUL-1024x779.jpg",
        "https://www.anipedia.net/imagenes/que-comen-los-perros.jpg",
        "https://oftalmologovigo.com/wp-content/uploads/tener-vista-de-lince.jpg"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF1C1A1C),
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Elige tu avatar",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF760B45), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona una de las siguientes opciones para cambiar tu foto de perfil:",
                    color = Color(0xFF8A8A8F),
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatares.forEach { url ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C2A2C))
                                .clickable { onSeleccionar(url) },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = "Opción de avatar",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            if (avatarActual == url) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x66760B45)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}