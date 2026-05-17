package com.example.trama.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun EscribirReseñaCard(
    movieTitle: String,
    onDismiss: () -> Unit,
    onEnviarClick: (rating: Float, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1A1C), RoundedCornerShape(24.dp))
                .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TÍTULO DEL DIÁLOGO
            Text(
                text = "TU CRÍTICA",
                color = Color(0xFF760B45),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = movieTitle,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SELECTOR DE ESTRELLAS DE 1 A 5
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i <= rating) Color(0xFFFFC107) else Color(0xFF4E474E),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = i.toFloat() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO DE TEXTO ABIERTO (TextField normal personalizado)
            TextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("¿Qué te ha parecido la película? Cuéntaselo a tus amigos...", color = Color(0xFF8A8A8F), fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF121012),
                    unfocusedContainerColor = Color(0xFF121012),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF760B45),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTONES DE ACCIÓN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color(0xFF8A8A8F), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // BOTÓN DE ACCIÓN MODIFICADO
                Button(
                    onClick = {
                        if (rating > 0f) { // Ahora solo exige que la puntuación sea mayor que 0
                            onEnviarClick(rating, comment)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF760B45),
                        disabledContainerColor = Color(0xFF4E474E)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    enabled = rating > 0f // Habilitado solo con poner estrellas
                ) {
                    Text("Publicar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}