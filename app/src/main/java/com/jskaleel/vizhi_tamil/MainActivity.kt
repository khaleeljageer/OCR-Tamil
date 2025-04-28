package com.jskaleel.vizhi_tamil

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            Log.d("Color", "Color: ${MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)}")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:parent=resizable,navigation=buttons"
)
@Composable
fun GreetingPreview() {
    VizhiTamilTheme {
        Greeting("Android")
    }
}