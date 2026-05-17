package com.example.trama.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trama.ViewModel.UserViewModel

//header pagina pergil
@Composable
fun PerfilHeader(
    readOnly: Boolean,
    username: String?,
    editando: Boolean,
    followState: UserViewModel.FollowState,
    onEditClick: () -> Unit,
    onFollowClick: () -> Unit
) {

    val label = when {
        !readOnly && editando -> "Cancelar"
        !readOnly -> "Editar perfil"
        followState == UserViewModel.FollowState.FOLLOWING -> "Siguiendo"
        followState == UserViewModel.FollowState.PENDING -> "Pendiente"
        else -> "Seguir"
    }

    val buttonColor = when {
        !readOnly -> Color(0xFF1C1A1C)
        followState == UserViewModel.FollowState.NONE -> Color(0xFF760B45)
        else -> Color(0xFF2C2A2C)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = if (readOnly) "ESPACIO DE" else "MI ESPACIO",
                color = Color(0xFF760B45),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = username?.let { "@$it" } ?: "@usuario",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Button(
            onClick = {
                if (readOnly) onFollowClick()
                else onEditClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {
            Text(label, color = Color.White)
        }
    }
}