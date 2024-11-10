package com.xdroid.app.changewallpaper.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.xdroid.app.changewallpaper.ui.theme.shimmerColor1
import com.xdroid.app.changewallpaper.ui.theme.shimmerColor2
import com.xdroid.app.changewallpaper.ui.theme.shimmerColor3


@Composable
fun ShimmerAnimationRow() {
    val shimmer = rememberShimmer(ShimmerBounds.Custom)
    Column(
        modifier = Modifier
            .height(200.dp)
            .padding(horizontal = 8.dp)
            .width(120.dp)
            .shimmer(rememberShimmer(shimmerBounds = ShimmerBounds.Window))// Adjust the size as needed
    ) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(shimmerColor1) // Adjust the size as needed
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {

                Box(
                    modifier = Modifier
                        .background(shimmerColor2)
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(shimmerColor3)
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }
        }
    }
}


@Composable
fun LoadingContent() {
    // Get the screen width
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Assuming a fixed item width, adjust the number of columns based on screen width
    val itemWidth = 120.dp // Set the desired width for each item
    val count = (screenWidth / itemWidth).toInt()
    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(count),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(count = 10){
                SingleShimmer()
            }
        }




    }
}

@Composable
fun SingleShimmer() {
    Column(
        modifier = Modifier
            .height(250.dp)
            .width(300.dp)
            .shimmer(rememberShimmer(shimmerBounds = ShimmerBounds.Window))// Adjust the size as needed
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .height(250.dp)
                .width(300.dp)
                .background(shimmerColor1) // Adjust the size as needed
        ) {
        }
    }
}

