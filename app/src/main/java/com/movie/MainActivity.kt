package com.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movie.ui.screen.AuthScreen
import com.movie.ui.screen.FavouriteScreen
import com.movie.ui.screen.MovieDetailScreen
import com.movie.ui.screen.MovieListScreen
import com.movie.ui.theme.MovieAppTheme
import com.movie.viewmodel.AuthViewModel
import com.movie.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                MovieApp()
            }
        }
    }
}

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val application = (LocalContext.current.applicationContext as android.app.Application)

    // ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.factory(application)
    )
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModel.factory(application)
    )

    // If already logged in → go straight to home, else auth
    val startDestination = if (authViewModel.isLoggedIn) "home" else "auth"

    val bottomNavItems = listOf(
        Triple("home", "Home", Icons.Filled.Home),
        Triple("favourites", "Favourites", Icons.Filled.Favorite)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute == "home" || currentRoute == "favourites"

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { (route, label, icon) ->
                            NavigationBarItem(
                                selected = currentRoute == route,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable("auth") {
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    MovieListScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { movieId ->
                            navController.navigate("movie_detail/$movieId")
                        }
                    )
                }

                composable("favourites") {
                    FavouriteScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { movieId ->
                            navController.navigate("movie_detail/$movieId")
                        }
                    )
                }

                composable(
                    route = "movie_detail/{movieId}",
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                    MovieDetailScreen(
                        movieId = movieId,
                        viewModel = movieViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}