package com.example.trama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trama.Components.NetworkMonitor
import com.example.trama.Screen.AuthScreen
import com.example.trama.Screen.NavigationWrapper
import com.example.trama.Screen.SinConexionScreen
import com.example.trama.ViewModel.AuthViewModel
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ui.theme.TramaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TramaTheme {
                val context = LocalContext.current
                val networkMonitor = remember { NetworkMonitor(context) }
                val tieneInternet by networkMonitor.isConnected.collectAsState(initial = true)

                if (!tieneInternet) {
                    SinConexionScreen()
                } else {
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
                        val activityContext = LocalContext.current

                        AuthScreen(
                            viewModel = authViewModel,
                            activityContext = activityContext
                        )
                    }
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