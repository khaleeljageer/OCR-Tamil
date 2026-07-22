package com.jskaleel.vizhi_tamil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jskaleel.vizhi_tamil.ui.screens.about.AboutScreen
import com.jskaleel.vizhi_tamil.ui.screens.home.HomeScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.imageDetail.ImageOCRDetailScreenRoute

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
    ) {
        composable<AppRoute.Home> {
            HomeScreenRoute(
                onOpenDetail = { scanId ->
                    navController.navigate(AppRoute.ImageOcrDetail(scanId))
                },
            )
        }
        composable<AppRoute.About> {
            AboutScreen()
        }
        composable<AppRoute.ImageOcrDetail> {
            // The ViewModel reads its scanId argument from SavedStateHandle.
            ImageOCRDetailScreenRoute(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
