package com.jskaleel.vizhi_tamil.ui.screens.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.data.BottomBarItem
import com.jskaleel.vizhi_tamil.ui.core.BottomNavigationBar
import com.jskaleel.vizhi_tamil.ui.navigation.NavigationHost
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ),
            SystemBarStyle.light(
                Color.argb(0xFF, 0xD9, 0xD2, 0xBF),
                Color.argb(0xFF, 0xD9, 0xD2, 0xBF)
            ),
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
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarItems = listOf(
        BottomBarItem(
            title = "Home",
            icon = painterResource(id = R.drawable.rounded_dashboard_24),
            route = "home"
        ),
        BottomBarItem(
            title = "About",
            icon = painterResource(id = R.drawable.rounded_info_24),
            route = "about"
        )
    )

    // Show bottom bar only on home screen
    val showBottomBar = bottomBarItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavigationBar(
                        items = bottomBarItems,
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavigationHost(
                navController = navController,
            )
        }
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