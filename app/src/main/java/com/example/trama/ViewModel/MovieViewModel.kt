package com.example.trama.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trama.Data.ApiService
import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.MovieSearchResponse
import com.example.trama.Data.Model.Review
import com.example.trama.State.MovieState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val api = ApiService.getInstance()
    private val reviewsRef get() = db.getReference("Reseñas")

    var state by mutableStateOf(MovieState())
        private set

    var selectedMovie by mutableStateOf<Movie?>(null)
        private set

    var movieReviews by mutableStateOf<List<Review>>(emptyList())
        private set

    private var movieReviewsListener: ValueEventListener? = null

    // ── Selección y reseñas de película ────────────────────────────────

    fun selectMovie(movie: Movie) {
        selectedMovie = movie
        observeMovieReviews(movie.id)
    }

    private fun observeMovieReviews(movieId: Int) {
        movieReviewsListener?.let { reviewsRef.removeEventListener(it) }

        movieReviewsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieReviews = snapshot.children
                    .mapNotNull { it.getValue(Review::class.java) }
                    .sortedByDescending { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        reviewsRef.orderByChild("movieId").equalTo(movieId.toDouble())
            .addValueEventListener(movieReviewsListener!!)
    }

    // ── Búsqueda y populares ────────────────────────────────────────────

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            state = state.copy(searchResults = emptyList())
            return
        }
        state = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response = api.searchMovies(query)

                // CORRECCIÓN: Extraemos la lista 'results' que viene dentro del response
                state = state.copy(
                    searchResults = response.results, // <--- Añade el .results aquí
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(error = e.localizedMessage, isLoading = false)
            }
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val response = api.getPopularMovies()
                state = state.copy(isLoading = false, movies = response.results)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = "Error cargando populares")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        movieReviewsListener?.let { reviewsRef.removeEventListener(it) }
    }
}