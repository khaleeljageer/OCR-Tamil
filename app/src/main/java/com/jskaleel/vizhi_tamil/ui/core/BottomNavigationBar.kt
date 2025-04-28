package com.jskaleel.vizhi_tamil.ui.core

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jskaleel.vizhi_tamil.data.BottomBarItem

@Composable
fun BottomNavigationBar(
    items: List<BottomBarItem>,
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon, contentDescription = item.title,
                        tint = if (currentRoute == item.route) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                },
                label = {
                    Text(
                        item.title, color = if (currentRoute == item.route) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "dashboard") {
                        navController.popBackStack("dashboard", inclusive = false)
                    }
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                            // Pop up backstack to the first destination and save state. This makes going back
                            // to the start destination when pressing back in any other bottom tab.
                            popUpTo("dashboard") {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}