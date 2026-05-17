package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.Review

private val Surface = Color(0xFF1C1A1C)
private val Accent = Color(0xFF760B45)

//componentes varios de pelis

//fila pelis
@Composable
fun MovieRow(movie: Movie) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .size(width = 56.dp, height = 84.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = movie.releaseDate?.take(4) ?: "",
                color = Color(0xFF8A8A8F),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(String.format("%.1f", movie.voteAverage), color = Color(0xFF8A8A8F), fontSize = 13.sp)
                if (!movie.genresText.isNullOrBlank()) {
                    Text(" · ", color = Color(0xFF8A8A8F), fontSize = 13.sp)
                    Text(
                        text = movie.genresText,
                        color = Accent,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

//tarjeta reseña, si es el usuario actual puede editar y eliminar
@Composable
fun ReviewCard(
    review: Review,
    readOnly: Boolean = false,
    onEdit: (newRating: Float, newComment: String) -> Unit = { _, _ -> },
    onDelete: () -> Unit = {}
) {
    var mostrarDialogoEdicion by remember { mutableStateOf(false) }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Título + acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = review.movieTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!readOnly) {
                Row {
                    IconButton(
                        onClick = { mostrarDialogoEdicion = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFF8A8A8F), modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { mostrarDialogoConfirmacion = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFF8A8A8F), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = if (index < review.rating.toInt()) Color(0xFFFFC107) else Color(0xFF3A3A3A),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
            Text("${review.rating}/5", color = Color(0xFF8A8A8F), fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))
        Text(review.comment, color = Color(0xFFD1D1D6), fontSize = 14.sp)
    }

    if (mostrarDialogoEdicion) {
        EditReviewDialog(
            review = review,
            onDismiss = { mostrarDialogoEdicion = false },
            onConfirm = { newRating, newComment ->
                onEdit(newRating, newComment)
                mostrarDialogoEdicion = false
            }
        )
    }

    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Eliminar reseña", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Seguro que quieres eliminar tu crítica de \"${review.movieTitle}\"? Esta acción no se puede deshacer.",
                    color = Color(0xFF8A8A8F)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    mostrarDialogoConfirmacion = false
                }) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar", color = Color(0xFF8A8A8F))
                }
            }
        )
    }
}

//tarjeta reseñas ajenas
@Composable
fun ReviewFeedCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "@${review.username.ifBlank { "usuario" }}",
                color = Accent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${review.rating.toInt()}/5",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = review.movieTitle,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (review.comment.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = review.comment,
                color = Color(0xFF8A8A8F),
                fontSize = 13.sp,
                maxLines = 3, //max 3 lineas
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

//dialog editar reseña
@Composable
private fun EditReviewDialog(
    review: Review,
    onDismiss: () -> Unit,
    onConfirm: (Float, String) -> Unit
) {
    var rating by remember { mutableStateOf(review.rating) }
    var comment by remember { mutableStateOf(review.comment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Editar crítica", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Column {
                    Text("Puntuación", color = Color(0xFF8A8A8F), fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(5) { index ->
                            val starValue = (index + 1).toFloat()
                            IconButton(
                                onClick = { rating = starValue },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = if (index < rating.toInt()) Color(0xFFFFC107) else Color(0xFF3A3A3A),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${rating.toInt()}/5",
                            color = Color(0xFF8A8A8F),
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2C2A2C),
                        unfocusedContainerColor = Color(0xFF2C2A2C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = Color(0xFFFEE2FF),
                        unfocusedLabelColor = Color(0xFF8A8A8F),
                        cursorColor = Accent
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (comment.isNotBlank()) onConfirm(rating, comment) },
                enabled = comment.isNotBlank()
            ) {
                Text("Guardar", color = Accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF8A8A8F))
            }
        }
    )
}

//estado vacio
@Composable
fun EmptyState(mensaje: String, submensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(mensaje, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(submensaje, color = Color(0xFF8A8A8F), fontSize = 13.sp)
    }
}