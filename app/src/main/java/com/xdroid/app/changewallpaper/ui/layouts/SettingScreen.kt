package com.xdroid.app.changewallpaper.ui.layouts

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.gson.Gson
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.cmodel.ListItems
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.ui.adscreen.RewardedAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.Amazon
import com.xdroid.app.changewallpaper.ui.components.GooglePlay
import com.xdroid.app.changewallpaper.ui.components.openLinkInBrowser
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogImage
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.backGroundColor
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.vm.SharedViewModel
import org.koin.compose.koinInject
import java.util.Random
import kotlin.text.Typography.quote

@Composable
fun SettingScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .padding(horizontal = 16.dp)
    ) {
        Header(navController)
        Spacer(Modifier.height(12.dp))
        ProfileMenu(icon = Icons.Default.Favorite, name = "Favorites") {
            navController.navigate(ScreenName.Favorites)
        }
        Spacer(Modifier.height(12.dp))
        ProfileMenu(icon = Icons.Default.Share, name = "Share App") {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this awesome app: https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                )
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Share via")
            )
        }

        Spacer(Modifier.height(12.dp))
        SettingsMenu(context)
    }
}

@Composable
private fun Header(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "BackIcon")
        }
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 18.sp,
            color = white,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun SettingsMenu(context: Context) {
    val settingsItems = listOf(
        SettingItem(
            icon = GooglePlay,
            title = "Google Apps",
            url = "https://play.google.com/store/apps/developer?id=Joyful+Juncture"
        ),
        SettingItem(
            icon = Amazon,
            title = "Amazon Apps",
            url = "https://www.amazon.com/s?i=mobile-apps&rh=p_4%3AJoyful%2BJuncture&search-type=ss"
        ),
        SettingItem(
            icon = Icons.Default.Reviews,
            title = "Review Our App Playstore",
            url = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        ),
        SettingItem(
            icon = Icons.Default.Info,
            title = "Version [${BuildConfig.VERSION_NAME}] (${BuildConfig.VERSION_CODE})",
            url = null
        )
    )

    LazyColumn {
        items(settingsItems.size) { index ->
            val item = settingsItems[index]
            ProfileMenu(icon = item.icon, name = item.title) {
                item.url?.let {
                    openLinkInBrowser(context, it)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val url: String?
)

@Composable
fun ProfileMenu(icon: ImageVector, name: String, action: () -> Unit = {}) {
    Column {
        Column(modifier = Modifier
            .background(white)
            .clickable { action() }) {
            Row(
                modifier = Modifier

                    .padding(vertical = 20.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(imageVector = icon, contentDescription = name, tint = black)
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = name, color = black
                )
            }

        }
        Spacer(Modifier.height(10.dp))
    }
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


@Composable
fun rememberReviewTask(reviewManager: ReviewManager): ReviewInfo? {
    var reviewInfo: ReviewInfo? by remember {
        mutableStateOf(null)
    }
    reviewManager.requestReviewFlow().addOnCompleteListener {
        if (it.isSuccessful) {
            reviewInfo = it.result
        }
    }


    return reviewInfo
}

@Composable
fun rememberFakeReviewTask(reviewManager: FakeReviewManager): ReviewInfo? {
    var reviewInfo: ReviewInfo? by remember {
        mutableStateOf(null)
    }
    reviewManager.requestReviewFlow().addOnCompleteListener {
        if (it.isSuccessful) {
            reviewInfo = it.result
        }
    }

    return reviewInfo
}
