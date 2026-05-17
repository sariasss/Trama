package com.example.trama.Data.Model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    val runtime: Int? = null, //duracion pelicula
    val genresText: String? = null
)

data class MovieSearchResponse(
    val results: List<Movie>
)