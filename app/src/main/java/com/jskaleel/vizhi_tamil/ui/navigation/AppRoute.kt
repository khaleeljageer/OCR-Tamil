package com.jskaleel.vizhi_tamil.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations. Each route is a [Serializable] type that
 * Navigation-Compose resolves via `composable<Route>` / `navController.navigate(route)`,
 * replacing the previous stringly-typed Screen/Route split.
 */
sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object About : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data class ImageOcrDetail(val scanId: Int) : AppRoute

    /** In-app viewer for a bundled legal/HTML asset (privacy policy, terms). */
    @Serializable
    data class LegalDoc(val asset: String, val title: String) : AppRoute
}
