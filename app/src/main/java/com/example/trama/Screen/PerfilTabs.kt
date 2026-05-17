package com.example.trama.Screen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.Review

//lista de reviews
fun LazyListScope.reseñasTab(
    reviews: List<Review>,
    readOnly: Boolean = false,
    onEdit: (reviewId: String, newRating: Float, newComment: String) -> Unit = { _, _, _ -> },
    onDelete: (reviewId: String) -> Unit = {}
) {
    if (reviews.isEmpty()) {
        item {
            EmptyState(
                mensaje = if (readOnly) "Este usuario aún no ha escrito críticas" else "Aún no has escrito ninguna crítica",
                submensaje = if (readOnly) "" else "Entra en una película y comparte tu opinión"
            )
        }
    } else {
        items(reviews, key = { it.id }) { review ->
            ReviewCard(
                review = review,
                readOnly = readOnly,
                onEdit = { newRating, newComment ->
                    onEdit(review.id, newRating, newComment)
                },
                onDelete = {
                    onDelete(review.id)
                }
            )
        }
    }
}

// lista favs
fun LazyListScope.favoritosTab(movies: List<Movie>, readOnly: Boolean = false) {
    if (movies.isEmpty()) {
        item {
            EmptyState(
                mensaje = if (readOnly) "Este usuario no tiene favoritas" else "No tienes películas favoritas",
                submensaje = if (readOnly) "" else "Pulsa el corazón en el detalle de una película"
            )
        }
    } else {
        items(movies, key = { it.id }) { movie ->
            MovieRow(movie = movie)
        }
    }
}

//lista vistas
fun LazyListScope.vistasTab(movies: List<Movie>, readOnly: Boolean = false) {
    if (movies.isEmpty()) {
        item {
            EmptyState(
                mensaje = if (readOnly) "Este usuario no ha visto películas" else "Todavía no has marcado películas",
                submensaje = if (readOnly) "" else "Pulsa el ojo en el detalle de una película"
            )
        }
    } else {
        items(movies, key = { it.id }) { movie ->
            MovieRow(movie = movie)
        }
    }
}