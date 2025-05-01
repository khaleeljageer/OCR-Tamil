package com.jskaleel.vizhi_tamil.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.vizhi_tamil.ui.screens.home.HomeScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.home.HomeViewModel
import com.jskaleel.vizhi_tamil.ui.screens.imageDetail.ImageOCRDetailScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.imageDetail.ImageOCRDetailViewModel
import com.jskaleel.vizhi_tamil.ui.screens.setup.download.DownloadScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.setup.download.DownloadViewModel
import com.jskaleel.vizhi_tamil.ui.utils.InvokeOnce


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Setup.name
    ) {
        navigation(
            startDestination = Screen.Download.route,
            route = Route.Setup.name
        ) {
            composable(route = Screen.Download.route) {
                val viewModel: DownloadViewModel = hiltViewModel()
                DownloadScreenRoute(
                    onDownloadComplete = {
                        navController.navigate(Route.Main.name) {
                            popUpTo(Route.Setup.name) {
                                inclusive = true
                            }
                        }
                    },
                    viewModel = viewModel,
                )
            }
        }
        mainNavGraph(navController = navController)
    }
}

private fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Home.route,
        route = Route.Main.name
    ) {
        composable(route = Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreenRoute(
                imageOCRDetail = { path ->
                    navController.navigate(Screen.ImageOCRDetail.Link.create(path))
                },
                viewModel = viewModel
            )
        }
        composable(route = Screen.About.route) {
            Column {
                Text(text = Screen.About.route)
            }
        }
        composable(route = Screen.ImageOCRDetail.Link.link) { entry ->
            val viewModel: ImageOCRDetailViewModel = hiltViewModel()
            InvokeOnce {
                val imagePath = Screen.ImageOCRDetail.Link.get(entry.arguments)
                viewModel.setup(imagePath = imagePath)
            }
            ImageOCRDetailScreenRoute(
                viewModel = viewModel
            )
        }
    }
}
