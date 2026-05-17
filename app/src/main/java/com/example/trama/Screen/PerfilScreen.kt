package com.example.trama.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.trama.Components.AvatarDialog
import com.example.trama.Components.PerfilHeader
import com.example.trama.Components.favoritosTab
import com.example.trama.Components.reseñasTab
import com.example.trama.Components.vistasTab
import com.example.trama.ViewModel.AuthViewModel
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ViewModel.UserViewModel

private val BgDark  = Color(0xFF121012)
private val Surface = Color(0xFF1C1A1C)
private val Accent  = Color(0xFF760B45)

@Composable
fun PerfilScreen(
    userId: String? = null,
    readOnly: Boolean = false,
    authViewModel: AuthViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onNavigateToUserProfile: (String) -> Unit
) {
    val firebaseUser = authViewModel.state.user

    LaunchedEffect(userId, readOnly, firebaseUser) {
        Log.d("DEBUG_TRAMA", "userId recibido: '$userId' | readOnly: $readOnly")

        if (readOnly) {
            if (userId != null) {
                userViewModel.loadExternalUserData(userId)
                userViewModel.checkFollowState(userId)
            }
        } else {
            if (firebaseUser != null) {
                userViewModel.initUserSession(authenticatedUid = firebaseUser.uid)
            }
        }
    }

    val perfil = if (readOnly) userViewModel.viewedUserProfile else userViewModel.currentUserProfile

    var editando by remember { mutableStateOf(false) }
    var username by remember(perfil) { mutableStateOf(perfil?.username.orEmpty()) }
    var biography by remember(perfil) { mutableStateOf(perfil?.biography.orEmpty()) }
    var avatarTemporalUrl by remember(perfil) { mutableStateOf(perfil?.profilePicture.orEmpty()) }
    var mostrarDialogoFotos by remember { mutableStateOf(false) }
    var pestañaActiva by remember { mutableStateOf("RESEÑAS") }

    var mostrarDialogoSeguidores by remember { mutableStateOf(false) }
    var mostrarDialogoSeguidos by remember { mutableStateOf(false) }

    val listaUidsMostrar = remember(mostrarDialogoSeguidores, mostrarDialogoSeguidos, perfil) {
        if (mostrarDialogoSeguidores) perfil?.followers?.keys?.toList().orEmpty()
        else perfil?.following?.keys?.toList().orEmpty()
    }

    val reviews = if (readOnly) userViewModel.viewedUserReviews else userViewModel.userReviews
    val favorites = if (readOnly) userViewModel.viewedUserFavorites else userViewModel.favoritesDetails
    val watched = if (readOnly) userViewModel.viewedUserWatched else userViewModel.watchedMoviesDetails

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // header
            item {
                PerfilHeader(
                    readOnly = readOnly,
                    username = perfil?.username,
                    editando = editando,
                    followState = userViewModel.followState,
                    onEditClick = {
                        if (editando) {
                            username = perfil?.username.orEmpty()
                            biography = perfil?.biography.orEmpty()
                            avatarTemporalUrl = perfil?.profilePicture.orEmpty()
                        }
                        editando = !editando
                    },
                    onFollowClick = {
                        userId?.let {
                            when (userViewModel.followState) {
                                UserViewModel.FollowState.NONE -> userViewModel.sendFollowRequest(it)
                                UserViewModel.FollowState.FOLLOWING -> userViewModel.unfollowUser(it)
                                UserViewModel.FollowState.PENDING -> Unit
                            }
                        }
                    }
                )
            }

            // avatar y seguidores/seguidos
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val profile = if (readOnly) userViewModel.viewedUserProfile else userViewModel.currentUserProfile

                    val totalSeguidores = profile?.followers?.size ?: 0
                    val totalSeguidos   = profile?.following?.size ?: 0

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Surface)
                            .clickable(enabled = editando && !readOnly) { mostrarDialogoFotos = true },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarTemporalUrl.ifBlank {
                                "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"
                            },
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (editando) {
                            Box(Modifier.fillMaxSize().background(Color(0x66000000)),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White)
                            }
                        }
                    }

                    // btn seguidores
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = totalSeguidores > 0) { mostrarDialogoSeguidores = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$totalSeguidores",
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Text("seguidores", color = Color(0xFF8A8A8F), fontSize = 11.sp)
                    }

                    // btn seguidos
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = totalSeguidos > 0) { mostrarDialogoSeguidos = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$totalSeguidos",
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Text("siguiendo", color = Color(0xFF8A8A8F), fontSize = 11.sp)
                    }
                }
            }

            // solicitudes
            item {
                if (!readOnly) {
                    val pendientes = userViewModel.pendingRequestUids
                    if (pendientes.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .background(Surface, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("SOLICITUDES", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Black)

                            pendientes.forEach { uid ->
                                var nombre by remember(uid) { mutableStateOf(uid.take(6)) }
                                LaunchedEffect(uid) {
                                    userViewModel.fetchUsernameForUid(uid) { nombre = it }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("@$nombre", color = Color.White, fontSize = 14.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { userViewModel.acceptFollowRequest(uid) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) { Text("Aceptar", fontSize = 12.sp) }
                                        OutlinedButton(
                                            onClick = { userViewModel.rejectFollowRequest(uid) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) { Text("Rechazar", fontSize = 12.sp, color = Color.White) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // bio y edicion
            item {
                if (!editando) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .background(Surface, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("BIOGRAFÍA", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(4.dp))
                        Text(biography.ifBlank { "Sin biografía aún." }, color = Color.White)
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
                        TextField(value = biography, onValueChange = { biography = it }, label = { Text("Biografía") }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                userViewModel.saveUserProfile(username.trim(), biography.trim(), avatarTemporalUrl.trim())
                                editando = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("GUARDAR") }
                    }
                }
            }

            // logout
            item {
                if (!editando && !readOnly) {
                    Text(
                        "Cerrar sesión",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .clickable {
                                userViewModel.clearUserData()
                                authViewModel.logout()
                            }
                    )
                }
            }

            // pestañas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("RESEÑAS", "FAVORITOS", "VISTAS").forEach { tab ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { pestañaActiva = tab }
                        ) {
                            Text(tab,
                                color = if (pestañaActiva == tab) Color.White else Color.Gray,
                                fontWeight = if (pestañaActiva == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp)
                            if (pestañaActiva == tab) {
                                Spacer(Modifier.height(4.dp))
                                Box(Modifier.width(40.dp).height(2.dp).background(Accent, RoundedCornerShape(1.dp)))
                            }
                        }
                    }
                }
            }

            item {
                Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(1.dp).background(Surface))
            }

            // contenido de pestañas
            when (pestañaActiva) {
                "RESEÑAS" -> reseñasTab(
                    reviews  = reviews,
                    readOnly = readOnly,
                    onEdit   = { id, rating, comment -> userViewModel.editReview(id, rating, comment) },
                    onDelete = { id -> userViewModel.deleteReview(id) }
                )
                "FAVORITOS" -> favoritosTab(movies = favorites, readOnly = readOnly)
                "VISTAS"    -> vistasTab(movies = watched, readOnly = readOnly)
            }
        }
    }

    if (mostrarDialogoFotos) {
        AvatarDialog(
            avatarActual = avatarTemporalUrl,
            onSeleccionar = { url -> avatarTemporalUrl = url; mostrarDialogoFotos = false },
            onDismiss = { mostrarDialogoFotos = false }
        )
    }

    // dialog seguidores y seguidos
    if (mostrarDialogoSeguidores || mostrarDialogoSeguidos) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoSeguidores = false
                mostrarDialogoSeguidos = false
            },
            containerColor = Surface,
            title = {
                Text(
                    text = if (mostrarDialogoSeguidores) "SEGUIDORES" else "SIGUIENDO",
                    color = Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                if (listaUidsMostrar.isEmpty()) {
                    Text("No hay usuarios en esta lista.", color = Color.White)
                } else {
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(listaUidsMostrar.size) { index ->
                                val targetUid = listaUidsMostrar[index]
                                var nombreUsuario by remember(targetUid) { mutableStateOf(targetUid.take(6)) }

                                LaunchedEffect(targetUid) {
                                    userViewModel.fetchUsernameForUid(targetUid) { nombreUsuario = it }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BgDark)
                                        .clickable {
                                            mostrarDialogoSeguidores = false
                                            mostrarDialogoSeguidos = false

                                            //para ir al usuario
                                            onNavigateToUserProfile(targetUid)
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "@$nombreUsuario",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoSeguidores = false
                        mostrarDialogoSeguidos = false
                    }
                ) {
                    Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}