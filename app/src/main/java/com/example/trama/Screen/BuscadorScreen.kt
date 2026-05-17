package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.User
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ViewModel.UserViewModel

//buscador peliculas por nombre, genero y buscador @usuario
@Composable
fun BuscadorScreen(
    movieViewModel: MovieViewModel,
    userViewModel: UserViewModel,
    onNavigateToDetalle: (Movie) -> Unit,
    onNavigateToUser: (String) -> Unit
) {
    val movieState = movieViewModel.state
    val userState  = userViewModel.state

    var query by remember { mutableStateOf("") }
    val modoUsuarios = query.startsWith("@")

    val categorias = listOf("Acción", "Drama", "Comedia", "Ciencia ficción", "Terror", "Romance")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121012))
            .padding(top = 36.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text("EXPLORAR", color = Color(0xFF760B45), fontSize = 12.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("Busca películas o usuarios", color = Color.White, fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 4.dp))
            Text(
                text = "Escribe @nombre para buscar usuarios",
                color = Color(0xFF8A8A8F), fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TextField(
                value = query,
                onValueChange = { nuevo ->
                    query = nuevo
                    if (nuevo.startsWith("@")) {
                        userViewModel.searchUsers(nuevo.removePrefix("@"))
                    } else {
                        movieViewModel.searchMovies(nuevo)
                    }
                },
                placeholder = {
                    Text("Título de película o @usuario...", color = Color(0xFF8A8A8F))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = Color(0xFF1C1A1C),
                    unfocusedContainerColor = Color(0xFF1C1A1C),
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    cursorColor             = Color(0xFF760B45),
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // CATEGORÍAS — solo cuando no estamos buscando usuarios
        if (!modoUsuarios) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categorias.size) { index ->
                    val cat = categorias[index]
                    Surface(
                        color = Color(0xFF1C1A1C),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable {
                            movieViewModel.searchMovies(cat)
                            query = cat
                        }
                    ) {
                        Text(cat, color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 13.sp)
                    }
                }
            }
        }

        // RESULTADOS
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (modoUsuarios) {
                ResultadosUsuarios(
                    isLoading  = userState.isSearchingUsers,
                    results    = userState.userSearchResults,
                    query      = query.removePrefix("@"),
                    onNavigateToUser = onNavigateToUser
                )
            } else {
                ResultadosPeliculas(
                    isLoading  = movieState.isLoading,
                    error      = movieState.error,
                    movies     = movieState.searchResults,
                    query      = query,
                    onNavigateToDetalle = onNavigateToDetalle
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ResultadosPeliculas(
    isLoading: Boolean,
    error: String?,
    movies: List<Movie>,
    query: String,
    onNavigateToDetalle: (Movie) -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator(color = Color(0xFF760B45),
            modifier = Modifier.align(Alignment.Center))
        error != null -> Text(error, color = Color(0xFFEF5350),
            modifier = Modifier.align(Alignment.Center).padding(24.dp))
        movies.isEmpty() && query.isNotBlank() -> Text(
            "No se encontraron resultados para \"$query\"",
            color = Color(0xFF8A8A8F),
            modifier = Modifier.align(Alignment.Center).padding(24.dp))
        movies.isEmpty() -> Text("Busca películas o escribe @usuario.",
            color = Color(0xFF4E474E),
            modifier = Modifier.align(Alignment.Center).padding(24.dp))
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie, onClick = { onNavigateToDetalle(movie) })
            }
        }
    }
}

@Composable
private fun BoxScope.ResultadosUsuarios(
    isLoading: Boolean,
    results: List<User>,
    query: String,
    onNavigateToUser: (String) -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator(color = Color(0xFF760B45),
            modifier = Modifier.align(Alignment.Center))
        results.isEmpty() && query.isNotBlank() -> Text(
            "No se encontró ningún usuario con \"@$query\"",
            color = Color(0xFF8A8A8F),
            modifier = Modifier.align(Alignment.Center).padding(24.dp))
        results.isEmpty() -> Text("Escribe @nombre para buscar usuarios.",
            color = Color(0xFF4E474E),
            modifier = Modifier.align(Alignment.Center).padding(24.dp))
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(results) { user ->
                UserCard(user = user, onClick = { onNavigateToUser(user.uid) })
            }
        }
    }
}

@Composable
private fun UserCard(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1A1C), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = user.profilePicture.ifBlank {
                "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"
            },
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text("@${user.username.ifBlank { "usuario" }}", color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 15.sp)
            if (user.biography.isNotBlank())
                Text(user.biography, color = Color(0xFF8A8A8F), fontSize = 13.sp, maxLines = 1)
        }
    }
}