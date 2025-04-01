package com.xdroid.app.changewallpaper.ui.layouts

import android.app.Activity
import android.app.WallpaperManager
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Reviews
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
    navController: NavController,
    viewModel: SharedViewModel
) {

    val context = LocalContext.current
    val activity = context as? Activity

    val reviewManager = remember {
        ReviewManagerFactory.create(context)
    }

//    val reviewManager = remember {
//        FakeReviewManager(context)
//    }


//    val reviewInfo = rememberReviewTask(reviewManager)

    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
//    val reviewInfo = rememberFakeReviewTask(reviewManager)

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var buttonClicked by rememberSaveable { mutableStateOf(false) }
    val networkHelper: NetworkHelper = koinInject()
    var isDataLoaded by rememberSaveable { mutableStateOf(false) }
    var showRewards by rememberSaveable { mutableStateOf(false) }
    val myImage = viewModel.getUserData() ?: ListItems(items = emptyList())
    var finalImage by rememberSaveable { mutableStateOf("") }
    val myImages by rememberSaveable { mutableStateOf(ArrayList<MyItems>()) }
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Wallpaper Set Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Wallpaper Setting Cancelled!", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        if (networkHelper.isNetworkConnected()) {
            RewardedAdManager.loadAd(context)
        }
        myImages.addAll(myImage.items)
        DebugMode.e("Items in settings ${myImages.size}")

    }


    Column(
        modifier = Modifier.fillMaxSize().background(black)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "BackIcon")
                }
                Text(context.getString(R.string.app_name), fontSize = 18.sp, color = white)

            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 5.dp)
        ) {

            LazyColumn {
                item {
                    Spacer(Modifier.height(20.dp))
                    ProfileMenu(GooglePlay, "Google Apps") {
                        openLinkInBrowser(
                            context,
                            "https://play.google.com/store/apps/developer?id=Joyful+Juncture"
                        )
                    }
                    ProfileMenu(Amazon, "Amazon Apps") {
                        openLinkInBrowser(
                            context,
                            "https://www.amazon.com/s?i=mobile-apps&rh=p_4%3AJoyful%2BJuncture&search-type=ss"
                        )
                    }
                    ProfileMenu(Icons.Default.Reviews, "Review Our App Playstore") {
                        openLinkInBrowser(
                            context,
                            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID.toString()}"
                        )
                    }
//                    ProfileMenu(Icons.Default.Reviews, "Review Our App Amazon") {
//                        openLinkInBrowser(
//                            context,
//                            "https://www.amazon.com/gp/product/B0DT6RYXQS"
//                        )
//                    }
//                    ProfileMenu(Icons.Default.AdsClick, "Watch Ads for surprise") {
//                        buttonClicked = true
//
//                    }
                    ProfileMenu(Icons.Default.Info, "Version [${BuildConfig.VERSION_NAME}] (${BuildConfig.VERSION_CODE})")
                }
            }


        }

    }

    if (buttonClicked) {
        isLoading = true
        if (networkHelper.isNetworkConnected()) {

            activity?.let {
                RewardedAdManager.showAd(it) { _ ->
                    buttonClicked = false
                    isLoading = false
                    val ite = myImages[Random().nextInt(myImages.size)]
                    val image = "${ite.collectionID}/${ite.id}/${ite.image}"
                    finalImage = imageUrl + image
                    DebugMode.e("Items in Settings $finalImage")
                    showRewards = true
                }
            }
//            if (mInterstitialAd == null) {
//                loadInterstitial(context) { _ ->
//                    showInterstitial(context) {
//                        buttonClicked = false
//                        isLoading = false
//                    }
//                }
//
//            } else {
//                showInterstitial(context) {
//                    buttonClicked = false
//                    isLoading = false
//
//                }
//            }
        }
    }

    if (showRewards) {
        CustomAlertDialogImage(
            image = finalImage,
            confirmButtonText = "Change",
            dismissButtonText = "Cancel",
            onConfirmButtonClick = {
                showRewards = false
                changeWallpaper2(
                    context,
                    imageUrl = finalImage,
                    callback = { success ->
                        if (success)
                            Toast.makeText(context, "Wallpaper Changed", Toast.LENGTH_SHORT).show()
                    },
                    wallpaperManager = wallpaperManager,
                    launcher = wallpaperLauncher
                )
            }
        ) {
            showRewards = false
        }
    }


}

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
