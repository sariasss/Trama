package com.example.trama.State

import com.example.trama.Data.Model.Movie

data class MovieState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val searchResults: List<Movie> = emptyList(),
    val error: String? = null
)