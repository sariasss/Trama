package com.example.trama.ViewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trama.Data.Model.User
import com.example.trama.State.AuthState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

// Cambiamos a AndroidViewModel para usar el contexto de forma segura
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseDatabase.getInstance()
    private val context = application.applicationContext

    var state by mutableStateOf(AuthState())
        private set

    init {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            state = state.copy(user = currentUser, isSuccess = true)
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            state = state.copy(error = "Por favor, introduce tu correo y contraseña")
            return
        }
        state = state.copy(isLoading = true, error = null)
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state = state.copy(isLoading = false, user = auth.currentUser, isSuccess = true)
                } else {
                    val mensajeEnEspanol = when (task.exception) {
                        is FirebaseAuthInvalidUserException        -> "Este correo electrónico no está registrado."
                        is FirebaseAuthInvalidCredentialsException -> "La contraseña o el correo no son correctos."
                        else -> "Error al iniciar sesión. Inténtalo de nuevo."
                    }
                    state = state.copy(isLoading = false, error = mensajeEnEspanol)
                }
            }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            state = state.copy(error = "Por favor, rellena todos los campos")
            return
        }
        if (password.length < 6) {
            state = state.copy(error = "La contraseña debe tener un mínimo de 6 caracteres")
            return
        }
        state = state.copy(isLoading = true, error = null)
        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser!!
                    val username     = "user_${firebaseUser.uid.take(4).lowercase()}"

                    val newUser = User(
                        uid = firebaseUser.uid,
                        username = username,
                        email = email.trim(),
                        biography = "",
                        profilePicture = "",
                        followers = emptyMap(),
                        following = emptyMap(),
                        pendingRequests = emptyMap()
                    )

                    db.getReference("Usuarios")
                        .child(firebaseUser.uid)
                        .setValue(newUser)
                        .addOnSuccessListener {
                            state = state.copy(isLoading = false, user = firebaseUser, isSuccess = true)
                        }
                        .addOnFailureListener {
                            state = state.copy(isLoading = false, user = firebaseUser, isSuccess = true)
                        }
                } else {
                    val mensajeEnEspanol = when (task.exception) {
                        is FirebaseAuthUserCollisionException     -> "Este correo ya está asociado a otra cuenta."
                        is FirebaseAuthWeakPasswordException      -> "La contraseña debe tener al menos 6 caracteres."
                        is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
                        else -> "No se pudo completar el registro. Comprueba tu conexión."
                    }
                    state = state.copy(isLoading = false, error = mensajeEnEspanol)
                }
            }
    }

    // NUEVA FUNCIÓN: Maneja todo el flujo de CredentialManager desde el ViewModel
    fun iniciarGoogleSignIn(activityContext: android.content.Context) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            val credentialManager = CredentialManager.create(context)
            val serverClientId = "161560647231-pc8crbrl6ah9kc9u4nmu8cb4118glj6f.apps.googleusercontent.com"
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                // Nota: El método requiere el contexto de la Activity actual para renderizar el UI
                val result = credentialManager.getCredential(context = activityContext, request = request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) { // <-- CAMBIADO AQUÍ

                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    loginWithGoogle(googleIdTokenCredential.idToken)
                } else {
                    state = state.copy(isLoading = false, error = "Tipo de credencial no soportada.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                state = state.copy(isLoading = false, error = "Inicio de sesión cancelado o fallido.")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 1. ¡AQUÍ! El login fue un éxito, guardamos los datos en Realtime Database si es nuevo
                    registrarUsuarioEnDatabaseSiNoExiste()

                    // 2. Después cambiamos el estado de la app para que navegue a la interfaz principal
                    state = state.copy(isLoading = false, user = auth.currentUser, isSuccess = true)
                } else {
                    state = state.copy(isLoading = false, error = "No se pudo iniciar sesión con Firebase usando Google.")
                }
            }
    }

    fun registrarUsuarioEnDatabaseSiNoExiste() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)

        // Comprobamos si el nodo del usuario ya existe en Realtime Database
        databaseRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // Si NO existe, es su primera vez iniciando sesión con Google. lo creamos:
                val nuevoUsuario = mapOf(
                    "uid" to uid,
                    "username" to (currentUser.displayName ?: "User_${uid.take(4)}"),
                    "email" to currentUser.email.orEmpty(),
                    "biography" to "",
                    "profilePicture" to currentUser.photoUrl.toString() // Aprovechamos su foto de Google
                )

                databaseRef.setValue(nuevoUsuario).addOnSuccessListener {
                    Log.d("DEBUG_TRAMA", "Usuario creado con éxito en Realtime Database tras login de Google")
                }
            } else {
                Log.d("DEBUG_TRAMA", "El usuario ya existía en la base de datos, no hace falta sobrescribirlo")
            }
        }
    }
    fun logout() {
        auth.signOut()
        state = AuthState()
    }
}