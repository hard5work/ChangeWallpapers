package com.xdroid.app.changewallpaper.ui.layouts

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.getScreenHeight
import com.xdroid.app.changewallpaper.ui.components.getScreenWidth
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperChangerApp(imageUrl: String) {

    val networkHelper: NetworkHelper = koinInject()
    val wallpaperChanged = rememberSaveable{ mutableStateOf(false) }
    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
    val buttonClicked = rememberSaveable { mutableStateOf(false) }
    val isLoading = rememberSaveable { mutableStateOf(false) }

    if (networkHelper.isNetworkConnected()) {
        if (mInterstitialAd == null)
            loadInterstitial(LocalContext.current)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 8.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = "Image",
            modifier = Modifier.width(getScreenWidth().dp).height(getScreenHeight().dp),
            contentScale = ContentScale.FillHeight
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (!isLoading.value) {
            Button(
                onClick = {

                    buttonClicked.value = true
                    isLoading.value = true
                    /*  changeWallpaper(
                      imageUrl, { success ->
                          wallpaperChanged.value = success

                      },
                      wallpaperManager
                  )*/

                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                    Icon(imageVector = Icons.Filled.PublishedWithChanges, contentDescription = "Change Icons", tint = background)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Change Wallpaper", color = background, fontSize = 16.sp)
                }
            }
        } else {
            CircularProgressIndicator(color = Color.White)
        }

        if (buttonClicked.value) {
            if (networkHelper.isNetworkConnected()) {
                showInterstitial(LocalContext.current) {
                    buttonClicked.value = false
                    changeWallpaper(
                        imageUrl, { success ->
                            wallpaperChanged.value = success
                        },
                        wallpaperManager
                    )
                }
            } else {
                changeWallpaper(
                    imageUrl, { success ->
                        wallpaperChanged.value = success
                    },
                    wallpaperManager
                )

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (wallpaperChanged.value) {
            isLoading.value = false
            InfoAlertDialog(message = "Wallpaper changed successfully") {
                wallpaperChanged.value = false

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
