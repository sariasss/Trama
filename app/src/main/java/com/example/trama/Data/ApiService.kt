package com.example.trama.Data

import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.MovieSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = "653fa9eca845f6c757d4f9487a3427a2"
    ): MovieSearchResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @retrofit2.http.Path("movie_id") movieId: Int,
        @Query("language") language: String = "es-ES",
        @Query("api_key") apiKey: String = "653fa9eca845f6c757d4f9487a3427a2"
    ): Movie

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = "653fa9eca845f6c757d4f9487a3427a2"
    ): MovieSearchResponse

    companion object {
        private var apiService: ApiService? = null
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}