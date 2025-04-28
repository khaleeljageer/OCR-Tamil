package com.jskaleel.vizhi_tamil.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "setup") {
        composable(route = "setup") {
            Column {
                Text(text = "Setup")
            }
        }

        mainNavGraph(navController = navController)
    }
}

private fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "home",
        route = "main"
    ) {
        composable(route = "home") {
            Column {
                Text(text = "Home")
            }
        }
        composable(route = "about") {
            Column {
                Text(text = "About")
            }
        }
    }
}
