package com.jskaleel.vizhi_tamil.ui.screens.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.ui.core.BottomNavigationBar
import com.jskaleel.vizhi_tamil.ui.model.BottomBarItem
import com.jskaleel.vizhi_tamil.ui.navigation.AppRoute
import com.jskaleel.vizhi_tamil.ui.navigation.NavigationHost
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Auto styles adapt the status/navigation bar icons to light/dark,
        // matching VizhiTamilTheme which follows the system setting.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            VizhiTamilTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarItems = listOf(
        BottomBarItem(
            title = "Home",
            icon = painterResource(id = R.drawable.rounded_dashboard_24),
            route = AppRoute.Home,
        ),
        BottomBarItem(
            title = "About",
            icon = painterResource(id = R.drawable.rounded_info_24),
            route = AppRoute.About,
        ),
    )

    // Bottom bar is shown only on the top-level destinations, not on detail screens.
    val showBottomBar = bottomBarItems.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    items = bottomBarItems,
                    navController = navController,
                    currentDestination = currentDestination,
                )
            }
        },
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:parent=resizable,navigation=buttons"
)
@Composable
fun GreetingPreview() {
    VizhiTamilTheme {
        MainNavigation()
    }
}