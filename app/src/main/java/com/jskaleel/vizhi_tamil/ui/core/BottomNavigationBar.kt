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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.jskaleel.vizhi_tamil.ui.model.BottomBarItem

@Composable
fun BottomNavigationBar(
    items: List<BottomBarItem>,
    navController: NavController,
    currentDestination: NavDestination?,
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 6.dp,
    ) {
        items.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.hasRoute(item.route::class) } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.title,
                    )
                },
                label = { },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                ),
            )
        }
    }
}
