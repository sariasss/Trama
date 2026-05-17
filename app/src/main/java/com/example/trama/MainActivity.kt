package com.example.trama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trama.Screen.AuthScreen
import com.example.trama.Screen.NavigationWrapper
import com.example.trama.ViewModel.AuthViewModel
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ui.theme.TramaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TramaTheme {
                val authViewModel: AuthViewModel = viewModel()
                val movieViewModel: MovieViewModel = viewModel()
                val authState = authViewModel.state

                if (authState.isSuccess) {
                    LaunchedEffect(Unit) {
                        movieViewModel.loadPopularMovies()
                    }
                    NavigationWrapper(
                        authViewModel = authViewModel,
                        movieViewModel = movieViewModel
                    )
                } else {
                    // Ahora AuthScreen no pide 'onGoogleSignIn' porque
                    // el botón interno ejecuta directamente viewModel.iniciarGoogleSignIn(context)
                    AuthScreen(
                        viewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TramaTheme {
        Greeting("Android")
    }
}