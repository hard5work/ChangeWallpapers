package com.xdroid.app.changewallpaper.ui.layouts

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp



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
            items(count = 20){
                SingleShimmer()
            }
        }




    }
}

@Composable
fun SingleShimmer() {
    ShimmerPlaceholder()
}


@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    shimmerWidth: Float = 200f
) {
    // Define animation to shift the gradient position
    val transition = rememberInfiniteTransition(label = "Looping")
    val shimmerTranslate by transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = shimmerWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // Define the shimmer brush with a gradient
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 1f),
            Color.White.copy(alpha = 0.5f),
            Color.LightGray.copy(alpha = 1f)
        ),
        //TOPLEFTCORNER TO BOTTOMRIGHTCORNER
        start =  Offset(x = shimmerTranslate, y = shimmerTranslate),
        end =Offset(x = shimmerTranslate + shimmerWidth, y = shimmerTranslate + shimmerWidth)

    //Straight Linear
//        start = Offset(x = shimmerTranslate, y = 0f),
//        end = Offset(x = shimmerTranslate + shimmerWidth, y = 0f)
    )

    // Use Box with shimmer effect background
    Box(
        modifier = modifier
            .background(shimmerBrush)
            .fillMaxSize()
    )
}


@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    // Use shimmer effect on placeholder items
    Column(
        modifier = modifier
            .padding(16.dp)
            .width(350.dp)
    ) {
        ShimmerEffect(modifier = Modifier
            .height(220.dp)
            .fillMaxWidth()
        )

    }
}


@Composable
fun ShimmerAdPlaceHolder(
    modifier: Modifier = Modifier
) {
    // Use shimmer effect on placeholder items
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ShimmerEffect(modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
        )

    }
}


