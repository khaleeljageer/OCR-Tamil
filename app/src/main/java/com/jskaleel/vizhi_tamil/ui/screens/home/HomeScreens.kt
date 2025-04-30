package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.core.utils.CallBack

@Composable
fun HomeScreen(
    onNewClick: CallBack
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = onNewClick,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_scanner_24),
                    contentDescription = "Scan"
                )
            },
            text = { Text(text = "Scan") },
            containerColor = MaterialTheme.colorScheme.primary,
        )
    }
}