package com.example.task_2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.task_2.presentation.detail.NobelDetailScreen
import com.example.task_2.presentation.favorites.FavoritesScreen
import com.example.task_2.presentation.list.NobelListScreen
import com.example.task_2.presentation.login.LoginScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("list") {
            NobelListScreen(
                onLaureateClick = { id ->
                    navController.navigate("detail/$id")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                }
            )
        }
        composable("detail/{laureateId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("laureateId") ?: return@composable
            NobelDetailScreen(
                laureateId = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                onLaureateClick = { id ->
                    navController.navigate("detail/$id")
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
