package com.jskaleel.vizhi_tamil.ui.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jskaleel.vizhi_tamil.ui.model.BottomBarItem
import com.jskaleel.vizhi_tamil.ui.navigation.Route

@Composable
fun BottomNavigationBar(
    items: List<BottomBarItem>,
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                    )
                },
                label = { },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == Route.Main.name) {
                        navController.popBackStack(Route.Main.name, inclusive = false)
                    }
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Route.Main.name) {
                                saveState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            )
        }
    }
}