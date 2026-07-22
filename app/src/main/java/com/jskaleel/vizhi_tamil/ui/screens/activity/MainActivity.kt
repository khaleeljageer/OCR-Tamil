package com.jskaleel.vizhi_tamil.ui.screens.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.domain.model.ThemeMode
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
            val viewModel: MainViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            VizhiTamilTheme(darkTheme = themeMode.isDark()) {
                MainNavigation()
            }
        }
    }
}

@Composable
private fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
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
            title = "Settings",
            icon = rememberVectorPainter(Icons.Filled.Settings),
            route = AppRoute.Settings,
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