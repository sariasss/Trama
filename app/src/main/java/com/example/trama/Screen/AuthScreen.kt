package com.example.trama.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trama.ViewModel.AuthViewModel

//login, registro y logout, inicio google
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit = {}
) {
    val state = viewModel.state
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginTab by remember { mutableStateOf(true) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121012))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "TRAMA",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp
            )

            Text(
                text = if (isLoginTab) "Tu cartelera inteligente" else "Crea tu cuenta de cinéfilo",
                color = Color(0xFF8A8A8F),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1A1C), RoundedCornerShape(26.dp))
                    .padding(6.dp)
            ) {
                Button(
                    onClick = { isLoginTab = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoginTab) Color(0xFF760B45) else Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(22.dp),
                    elevation = null
                ) {
                    Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Button(
                    onClick = { isLoginTab = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoginTab) Color(0xFF760B45) else Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(22.dp),
                    elevation = null
                ) {
                    Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C1A1C),
                    unfocusedContainerColor = Color(0xFF1C1A1C),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF760B45),
                    unfocusedBorderColor = Color.Transparent,
                    focusedLabelColor = Color(0xFFFEE2FF),
                    unfocusedLabelColor = Color(0xFF8A8A8F),
                    cursorColor = Color(0xFF760B45)
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C1A1C),
                    unfocusedContainerColor = Color(0xFF1C1A1C),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF760B45),
                    unfocusedBorderColor = Color.Transparent,
                    focusedLabelColor = Color(0xFFFEE2FF),
                    unfocusedLabelColor = Color(0xFF8A8A8F),
                    cursorColor = Color(0xFF760B45)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (isLoginTab) viewModel.login(email, password)
                    else viewModel.register(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF760B45),
                    contentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isLoginTab) "Iniciar Sesión" else "Comenzar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2C2A2C))
                Text("o", color = Color(0xFF8A8A8F), fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2C2A2C))
            }

            // google
            OutlinedButton(
                onClick = { viewModel.iniciarGoogleSignIn(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF1C1A1C),
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3A3A))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "G",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                    Text(
                        text = "Continuar con Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = Color(0xFFEF5350),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}