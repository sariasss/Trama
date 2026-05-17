package com.example.trama.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.trama.ViewModel.AuthViewModel
import com.example.trama.ViewModel.MovieViewModel
import com.example.trama.ViewModel.UserViewModel

@Composable
fun NavigationWrapper(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val authState = authViewModel.state

    if (!authState.isSuccess || authState.user == null) {
        AuthScreen(viewModel = authViewModel, onAuthSuccess = {})
        return
    }

    AppNavigation(
        authViewModel = authViewModel,
        movieViewModel = movieViewModel,
        userViewModel = userViewModel
    )
}

@Composable
private fun AppNavigation(
    authViewModel: AuthViewModel,
    movieViewModel: MovieViewModel,
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = AppDestinations.Inicio.route
            ) {

                composable(AppDestinations.Inicio.route) {
                    InicioScreen(
                        movieViewModel = movieViewModel,
                        userViewModel = userViewModel,
                        onNavigateToDetalle = { movie ->
                            movieViewModel.selectMovie(movie)
                            navController.navigate(AppDestinations.Detalle.createRoute(movie.title))
                        }
                    )
                }

                composable(AppDestinations.Buscador.route) {
                    BuscadorScreen(
                        movieViewModel = movieViewModel,
                        userViewModel = userViewModel,
                        onNavigateToDetalle = { movie ->
                            movieViewModel.selectMovie(movie)
                            navController.navigate(AppDestinations.Detalle.createRoute(movie.title))
                        },
                        onNavigateToUser = { userId ->
                            navController.navigate(AppDestinations.PerfilUsuario.createRoute(userId))
                        }
                    )
                }

                composable(
                    route = AppDestinations.PerfilUsuario.route,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    PerfilScreen(
                        userId = userId,
                        readOnly = true,
                        authViewModel = authViewModel,
                        movieViewModel = movieViewModel,
                        userViewModel = userViewModel
                    )
                }

                composable(AppDestinations.Perfil.route) {
                    PerfilScreen(
                        authViewModel = authViewModel,
                        movieViewModel = movieViewModel,
                        userViewModel = userViewModel
                    )
                }

                composable(
                    route = AppDestinations.Detalle.route,
                    arguments = listOf(navArgument("title") { type = NavType.StringType })
                ) {
                    val movie = movieViewModel.selectedMovie
                    if (movie != null) {
                        DetalleScreen(
                            movie = movie,
                            onBack = { navController.popBackStack() },
                            movieViewModel = movieViewModel,
                            userViewModel = userViewModel,
                            onNavigateToUserProfile = { userId ->
                                navController.navigate(
                                    AppDestinations.PerfilUsuario.createRoute(userId)
                                )
                            }
                        )
                    }
                }
            }
        }

        BottomBar(navController)
    }
}