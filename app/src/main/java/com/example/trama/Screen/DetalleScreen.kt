package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.trama.Components.EscribirReseñaCard
import com.example.trama.Data.Model.Movie
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ViewModel.UserViewModel

//ver detalle de la peli, hacer reseña, ver reseñas, boton peli vista y boton fav
@Composable
fun DetalleScreen(
    movie: Movie,
    onBack: () -> Unit,
    movieViewModel: MovieViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onNavigateToUserProfile: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(movie.id) {
        movieViewModel.selectMovie(movie)
    }

    val yaVista    = userViewModel.watchedMoviesIds.contains(movie.id)
    val esFavorito = userViewModel.favoritesIds.contains(movie.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121012))
            .padding(top = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/original${movie.backdropPath}",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF121012)),
                                    startY = 200f
                                )
                            )
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier
                            .size(width = 120.dp, height = 180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(movie.title, color = Color.White,
                            fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Text(movie.genresText ?: "Película", color = Color(0xFF760B45),
                            fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
                            Spacer(Modifier.width(4.dp))

                            Text("${movie.voteAverage}/10", color = Color.White)
                            Spacer(Modifier.width(12.dp))

                            Icon(Icons.Default.DateRange, null, tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))

                            Text(movie.releaseDate?.take(4) ?: "N/A", color = Color.White)
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text("SINOPSIS", color = Color(0xFF760B45),
                        fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(movie.overview, color = Color(0xFFD1D1D6), fontSize = 16.sp)
                }
            }

            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF760B45)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("ESCRIBIR UNA RESEÑA", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Text("RESEÑAS", color = Color.White, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(24.dp))
            }

            items(movieViewModel.movieReviews) { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1A1C)),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        val miUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

                        //si es un comentario mio, no puedo entrar al perfil
                        if (review.userId == miUid) {
                        } else {
                            onNavigateToUserProfile(review.userId)
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, null,
                                tint = Color(0xFFFEE2FF), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("@${review.username}", color = Color(0xFFFEE2FF),
                                fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < review.rating.toInt())
                                        Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("${review.rating}/5", color = Color.Gray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(review.comment, color = Color(0xFFD1D1D6), fontSize = 14.sp)
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .background(Color(0x88121012), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }

        // boton peli vista
        IconButton(
            onClick = { userViewModel.toggleWatchedMovie(movie.id) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color(0x88121012), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Visibility, null,
                tint = if (yaVista) Color(0xFF760B45) else Color.White)
        }

        // boton peli favorita
        IconButton(
            onClick = { userViewModel.toggleFavorite(movie.id) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 64.dp)
                .background(Color(0x88121012), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Favorite, null,
                tint = if (esFavorito) Color.Red else Color.White)
        }

        // dialog escribir reseña
        if (showDialog) {
            EscribirReseñaCard(
                movieTitle = movie.title,
                onDismiss = { showDialog = false },
                onEnviarClick = { rating, comment ->
                    userViewModel.publishReview(
                        movieId = movie.id,
                        movieTitle = movie.title,
                        rating = rating,
                        comment = comment
                    )
                    showDialog = false
                }
            )
        }
    }
}