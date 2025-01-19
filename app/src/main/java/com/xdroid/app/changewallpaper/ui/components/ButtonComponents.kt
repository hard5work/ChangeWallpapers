package com.xdroid.app.changewallpaper.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chartboost.sdk.ads.Ad
import com.chartboost.sdk.ads.Banner
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.cmodel.AdItem
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.isNull
import kotlinx.coroutines.delay
import java.util.Random

@Composable
fun ButtonCompo(
    isPrimary: Boolean = true,
    label: String,
    onClick: () -> Unit,
    width: Int = responsiveWidth(baseWidth = 150)
) {

    if (isPrimary) {
        ButtonComponent(label = label, onClick = onClick, width = width)
    } else {
        OutlineButtonComponent(label = label, onClick = onClick, width = width)
    }


}

@Composable
fun OutlineButtonComponent(
    label: String,
    onClick: () -> Unit,
    width: Int = responsiveWidth(baseWidth = 150)
) {
    OutlinedButton(
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 8.dp),
        onClick = { onClick() },
        border = BorderStroke(1.dp, Color.Black), // Border color and width
        content = {
            Text(label)
        },


        shape = RoundedCornerShape(5.dp),
    )
}

@Composable
fun ButtonComponent(
    label: String,
    onClick: () -> Unit,
    width: Int = responsiveWidth(baseWidth = 150)
) {

    Button(
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 8.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(5.dp),

        ) {
        Text(label)
    }
}


@Composable
fun IconButtons(
    icon: ImageVector,
    modifier: Modifier,
    label: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.padding(end = 8.dp),
                tint = Color.White
            )
            Text(
                label,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }

}

@Composable
fun IconOutlineButton(
    icon: ImageVector,
    modifier: Modifier,
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { onClick() },
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.padding(end = 8.dp),
                tint = Color.White
            )
            Text(
                label,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }

}

@Composable
fun responsiveWidth(baseWidth: Dp): Dp {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    // Adjust the multiplier or add logic for min/max width as needed
    return remember(fontScale) { baseWidth * fontScale }
}

@Composable
fun responsiveWidth(baseWidth: Int): Int {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    // Adjust the multiplier or add logic for min/max width as needed
    return remember(fontScale) { baseWidth * fontScale }.toInt()
}

@Composable
fun getScreenWidth(): Float {
    // Access the current configuration to get the screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Calculate half of the screen width
    // Note: screenWidthDp.value gives you the float value of dp
    return screenWidthDp.value - 32
}

@Composable
fun getScreenHeight(): Float {
    // Access the current configuration to get the screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenHeightDp.dp

    // Calculate half of the screen width
    // Note: screenWidthDp.value gives you the float value of dp
    return screenWidthDp.value - 100
}


@Composable
fun getScreenWidthResponsive(): Float {
    // Access the current configuration to get the screen width
    val configuration = LocalConfiguration.current
    var screenWidthDp = configuration.screenWidthDp.dp
    val screenOrientation = configuration.orientation

    if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        screenWidthDp = configuration.screenHeightDp.dp
    }

    // Calculate half of the screen width
    // Note: screenWidthDp.value gives you the float value of dp
    return screenWidthDp.value - 38
}

@Composable
fun getWidthAlertImage(): Dp {
    // Access the current configuration to get the screen width
    val configuration = LocalConfiguration.current
    var screenWidthDp = 150f
    val screenOrientation = configuration.orientation

    if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        screenWidthDp = 50f
    }

    return screenWidthDp.dp
}

@Composable
fun getHalfScreenWidth(): Float {
    // Access the current configuration to get the screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Calculate half of the screen width
    // Note: screenWidthDp.value gives you the float value of dp
    return screenWidthDp.value / 2 - 38
}


fun isPortrait(configurationInfo: Configuration): Boolean {
    // Access the current configuration to get the screen orientation
    val screenOrientation = configurationInfo.orientation

    // Return true if the screen orientation is portrait
    return screenOrientation == Configuration.ORIENTATION_PORTRAIT
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AutoAdSliderNetwork(banner: List<AdItem>, modifier: Modifier = Modifier) {
    // State to keep track of the current image index
    var currentIndex by remember { mutableStateOf(0) }
    var banners: List<AdItem> by remember {
        mutableStateOf(listOf())
    }
    LaunchedEffect(banner.isNotEmpty()) {
        banners = activeList(banner).shuffled()
        DebugMode.e("asd test banners ${banners.size}")
    }
    if (banners.isNotEmpty()) {


        val context = LocalContext.current

        // Launch a timer using a LaunchedEffect with a key of the current index
        LaunchedEffect(key1 = currentIndex) {
            delay(5000L) // Delay for 5 seconds
            currentIndex =
                (currentIndex + 1) % banners.size // Move to the next image, loop back to the first
        }

        // Display the current image
        // AnimatedContent to handle slide animations


        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() with
                        slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
            },
            modifier = modifier.fillMaxSize()
        ) { targetIndex ->
            val imagePainter = banners[targetIndex]
            val images =
                UrlName.imageUrl + "${imagePainter.collectionID}/${imagePainter.id}/${imagePainter.imageFile}"
//        DebugMode.e("Ad Banners $images")
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { openLinkInBrowser(context, imagePainter.link.isNull()) }
            ) {

                NetworkImageAds(
                    url = images,
                    contentDescription = "Auto Slider Ads",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(18.dp))
                        .padding(vertical = 10.dp),
                )
            }
        }
    }
}


fun activeList(banners: List<AdItem>): List<AdItem> {
    return banners.filter { it.status.isNull() }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NetworkImageAds(
    url: String?,
    modifier: Modifier,
    contentDescription: String? = "Network Image"
) {
    val requestOptions = RequestOptions()
        .skipMemoryCache(true) // Skip memory cache
        .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip disk cache
    val context = LocalContext.current

    GlideImage(
        model = url,
        contentDescription = contentDescription,
        loading = placeholder(R.drawable.shimmer_shape),
        failure = placeholder(R.drawable.shimmer_shape),
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}


fun openLinkInBrowser(context: android.content.Context, url: String) {
    try {
        // Check if it's a Play Store URL
        if (url.contains("play.google.com/store/apps/details")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Direct to Play Store if available
            intent.setPackage("com.android.vending") // Play Store package name
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return
            }
        }

        // Fallback to browser if Play Store is not available
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(browserIntent)
        } else {
            Toast.makeText(context, "No application can handle this URL.", Toast.LENGTH_SHORT)
                .show()
        }
    } catch (e: Exception) {
        DebugMode.e("Error opening URL: ${e.message}")
        e.printStackTrace()
    }
}
