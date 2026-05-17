package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trama.Data.Model.Movie
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ViewModel.UserViewModel

//pagina principal, lista peliculas destacadas, tendencias, recomendadas y vemos reseñas de seguidos
@Composable
fun InicioScreen(
    movieViewModel: MovieViewModel,
    userViewModel: UserViewModel,
    onNavigateToDetalle: (Movie) -> Unit
) {
    val state = movieViewModel.state
    val userState = userViewModel.state

    LaunchedEffect(Unit) {
        userViewModel.loadFollowingFeed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121012))
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    color = Color(0xFF760B45),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null -> {
                Text(
                    text = state.error,
                    color = Color(0xFFEF5350),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text("TRAMA", color = Color(0xFF760B45), fontSize = 12.sp,
                                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Spacer(Modifier.height(6.dp))
                            Text("Descubre tu próxima película", color = Color.White,
                                fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Actualizado con lo más popular del momento",
                                color = Color(0xFF8A8A8F), fontSize = 14.sp)
                        }
                    }

                    // ver reseñas de seguidos
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                            Text("Actividad de seguidos", color = Color.White,
                                fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))

                            if (userState.followingFeed.isEmpty()) {
                                Text(
                                    text = "No hay actividad reciente de tus seguidos. ¡Busca cinéfilos en el explorador!",
                                    color = Color(0xFF4E474E),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    if (userState.followingFeed.isNotEmpty()) {
                        items(userState.followingFeed.take(5)) { review ->
                            ReviewFeedCard(
                                review = review,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    // pelis destacadas
                    item {
                        val featured = state.movies.firstOrNull()
                        if (featured != null) {
                            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                Text("Destacada", color = Color.White,
                                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                MovieCard(movie = featured, onClick = { onNavigateToDetalle(featured) })
                            }
                        }
                    }

                    // pelis tendencias
                    item {
                        Column {
                            Text("🔥 Tendencias", color = Color.White,
                                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 24.dp))
                            Spacer(Modifier.height(12.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.movies) { movie ->
                                    MovieCard(movie = movie, onClick = { onNavigateToDetalle(movie) })
                                }
                            }
                        }
                    }

                    // recomedadas
                    item {
                        Column {
                            Text("🎯 Recomendadas", color = Color.White,
                                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 24.dp))
                            Spacer(Modifier.height(12.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.movies.takeLast(10)) { movie ->
                                    MovieCard(movie = movie, onClick = { onNavigateToDetalle(movie) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}