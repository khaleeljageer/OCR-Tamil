package com.jskaleel.vizhi_tamil.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
    object Download : Screen("download")
}

sealed class Route(val name: String) {
    object Setup : Route("Route_Setup")
    object Main : Route("Route_Main")
}