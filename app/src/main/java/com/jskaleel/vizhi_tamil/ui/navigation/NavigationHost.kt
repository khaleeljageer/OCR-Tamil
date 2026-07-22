package com.jskaleel.vizhi_tamil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jskaleel.vizhi_tamil.ui.screens.about.AboutScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.about.LegalDocScreen
import com.jskaleel.vizhi_tamil.ui.screens.home.HomeScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.imageDetail.ImageOCRDetailScreenRoute
import com.jskaleel.vizhi_tamil.ui.screens.settings.SettingsScreenRoute

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
        composable<AppRoute.Settings> {
            SettingsScreenRoute()
        }
        composable<AppRoute.About> {
            AboutScreenRoute(
                onOpenLegal = { asset, title ->
                    navController.navigate(AppRoute.LegalDoc(asset, title))
                },
            )
        }
        composable<AppRoute.LegalDoc> { entry ->
            val route = entry.toRoute<AppRoute.LegalDoc>()
            LegalDocScreen(
                title = route.title,
                assetFile = route.asset,
                onBack = { navController.popBackStack() },
            )
        }
        composable<AppRoute.ImageOcrDetail> {
            // The ViewModel reads its scanId argument from SavedStateHandle.
            ImageOCRDetailScreenRoute(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
