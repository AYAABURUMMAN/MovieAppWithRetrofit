import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movie.ui.screen.MovieDetailScreen
import com.movie.ui.screen.MovieListScreen
import com.movie.viewmodel.MovieViewModel

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val viewModel: MovieViewModel = viewModel()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = "movie_list"
        ) {
            // شاشة الليست
            composable("movie_list") {
                MovieListScreen(
                    viewModel = viewModel,
                    onMovieClick = { movieId ->
                        navController.navigate("movie_detail/$movieId")
                    }
                )
            }

            // شاشة الديتيل
            composable(
                route = "movie_detail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}