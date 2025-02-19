package com.xdroid.app.changewallpaper.ui.layouts

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.getScreenHeight
import com.xdroid.app.changewallpaper.ui.components.getScreenWidth
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperChangerApp(navController: NavController, imageUrl: String) {

    val networkHelper: NetworkHelper = koinInject()
    var wallpaperChanged by rememberSaveable { mutableStateOf(false) }
    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
    var buttonClicked by rememberSaveable { mutableStateOf(false) }
    val isLoading = rememberSaveable { mutableStateOf(false) }
    var isDataLoaded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (networkHelper.isNetworkConnected()) {
            if (mInterstitialAd == null)
                loadInterstitial(context) { isAdLoaded ->
                    isDataLoaded = isAdLoaded
                }
        }
    }

    BackHandler {
        navController.navigateUp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 8.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = "Image",
            modifier = Modifier
                .width(getScreenWidth().dp)
                .height(getScreenHeight().dp),
            contentScale = ContentScale.FillHeight
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (!isLoading.value) {
            Button(
                onClick = {

                    buttonClicked = true
                    isLoading.value = true
                    /*  changeWallpaper(
                      imageUrl, { success ->
                          wallpaperChanged.value = success

                      },
                      wallpaperManager
                  )*/

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.PublishedWithChanges,
                        contentDescription = "Change Icons",
                        tint = background
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Change Wallpaper", color = background, fontSize = 16.sp)
                }
            }
        } else {
            CircularProgressIndicator(color = Color.White)
        }

        if (buttonClicked) {
            if (networkHelper.isNetworkConnected()) {
                DebugMode.e("Change the wallpaper?? -> >>>>")
                LaunchedEffect(Unit) {
                    if (mInterstitialAd == null) {
                        loadInterstitial(context) { _ ->
                            showInterstitial(context) {
                                buttonClicked = false
                                changeWallpaper2(
                                    context = context,
                                    imageUrl, { success ->
                                        wallpaperChanged = success

                                    },
                                    wallpaperManager
                                )
                            }
                        }

                    } else {
                        showInterstitial(context) {
                            buttonClicked = false
                            changeWallpaper2(
                                context = context,
                                imageUrl, { success ->
                                    wallpaperChanged = success

                                },
                                wallpaperManager
                            )
                        }
                    }
                }

            } else {
                buttonClicked = false
                changeWallpaper2(
                    context = context,
                    imageUrl, { success ->
                        wallpaperChanged = success
                    },
                    wallpaperManager
                )

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (wallpaperChanged) {
            InfoAlertDialog(message = "Wallpaper changed successfully") {
                isLoading.value = false
                wallpaperChanged = false
                removeInterstitial()

            }
        }
    }
}

private fun changeWallpaper(
    imageUrl: String,
    callback: (Boolean) -> Unit,
    wallpaperManager: WallpaperManager
) {
    // Launch a coroutine in the IO context to perform the network operation
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)

            withContext(Dispatchers.Main) {
                wallpaperManager.setBitmap(bitmap)
                callback(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                callback(false)
            }
        }
    }


}

private fun changeWallpaper2(
    context: Context,
    imageUrl: String,
    callback: (Boolean) -> Unit,
    wallpaperManager: WallpaperManager
) {
    // Ask the user where to set the wallpaper
    val options = arrayOf("Home Screen", "Lock Screen", "Both")

    // Show dialog on the main thread
    CoroutineScope(Dispatchers.Main).launch {
        val selectedOption = suspendCoroutine<Int> { continuation ->
            AlertDialog.Builder(context)
                .setTitle("Set Wallpaper")
                .setItems(options) { _, which -> continuation.resume(which) }
                .setCancelable(false)
                .show()
        }

        // Map selected option to WallpaperManager flags

        // Proceed to download and set wallpaper
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                withContext(Dispatchers.Main) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val wallpaperFlag = when (selectedOption) {
                            0 -> WallpaperManager.FLAG_SYSTEM
                            1 -> WallpaperManager.FLAG_LOCK
                            2 -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                            else -> WallpaperManager.FLAG_SYSTEM
                        }

                        wallpaperManager.setBitmap(bitmap, null, true, wallpaperFlag)
                    } else {
                        // For older devices, ignore the flag and set wallpaper for home screen
                        wallpaperManager.setBitmap(bitmap)
                    }
                    callback(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }
}
