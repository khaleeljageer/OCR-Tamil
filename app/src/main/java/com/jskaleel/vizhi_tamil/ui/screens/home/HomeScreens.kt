package com.jskaleel.vizhi_tamil.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.core.utils.CallBack
import com.jskaleel.vizhi_tamil.ui.theme.VizhiTamilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewClick: CallBack
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            RecentScanList()
        }

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

@Composable
fun RecentScanList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(100) {
            RecentScanItem()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RecentScanItem() {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
            ) {
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current).data("https://picsum.photos/200")
//                .crossfade(true)
//                .build(),
//            contentDescription = "Image",
//            modifier = Modifier.size(50.dp),
//            contentScale = ContentScale.Inside,
//        )

                Text("Hello")
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = "spec:parent=pixel_6",
)
@Composable
private fun HomeScreenPreview() {
    VizhiTamilTheme {
        HomeScreen(
            onNewClick = {}
        )
    }
}