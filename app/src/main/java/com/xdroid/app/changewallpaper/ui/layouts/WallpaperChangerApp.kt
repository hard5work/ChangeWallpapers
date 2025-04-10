package com.xdroid.app.changewallpaper.ui.layouts

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.chartboost.sdk.impl.wa
import com.google.android.gms.ads.nativead.NativeAd
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.ui.adscreen.AdmobNativeAd
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.getScreenHeight
import com.xdroid.app.changewallpaper.ui.components.getScreenWidth
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
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
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var counter by remember {
        mutableIntStateOf(App.preferenceHelper.getValue(PrefConstant.COUNTER, 0) as Int)
    }
    // Load the ad when the screen appears
    LaunchedEffect(Unit) {
        NativeAdManager.loadNativeAd(context) { ad ->
            nativeAd = ad
        }
    }
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        buttonClicked = false
        isLoading.value = false
        if (result.resultCode == Activity.RESULT_OK) {
            wallpaperChanged = true
//            Toast.makeText(context, "Wallpaper Set Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            wallpaperChanged = false

//            Toast.makeText(context, "Wallpaper Setting Cancelled!", Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(Unit) {
        if (networkHelper.isNetworkConnected()) {
            if (mInterstitialAd == null)
                loadInterstitial(context) { isAdLoaded ->
                    isDataLoaded = isAdLoaded
                }
        }
    }
    fun backHandle() {
        if (counter == 4) {
            if (mInterstitialAd == null) {
                loadInterstitial(context) { _ ->
                    showInterstitial(context) {
                        navController.navigateUp()
                    }
                }

            } else {
                showInterstitial(context) {
                    navController.navigateUp()
                }
            }
        } else {
            navController.navigateUp()
        }

    }

    BackHandler {
        backHandle()
    }



    Box(modifier = Modifier.fillMaxSize()) {

        GlideImage(
            model = imageUrl,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = {
                backHandle()
            },

            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                tint = white,
                contentDescription = "Back Button"
            )
        }

        Column(
            modifier = Modifier
//                .background(background)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            GlideImage(
//                model = imageUrl,
//                contentDescription = "Image",
//                modifier = Modifier
//                    .width(getScreenWidth().dp)
//                    .height(getScreenHeight().dp),
//                contentScale = ContentScale.FillHeight
//            )

            Spacer(modifier = Modifier.height(10.dp))
//        if (nativeAd == null) {
//            ShimmerAdPlaceHolder()
//        } else {
//            AdmobNativeAd(nativeAd)
//        }
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


            Spacer(modifier = Modifier.height(16.dp))

        }
    }

    if (wallpaperChanged) {
        InfoAlertDialogWithAds(
            message = "Wallpaper changed successfully",
            dismissOnBackPress = true,
            dismissOnClickedOutside = true
        ) {
            isLoading.value = false
            wallpaperChanged = false
            removeInterstitial()

        }
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
                                    if (!success) {
                                        isLoading.value = false
                                        wallpaperChanged = false
                                    }

                                },
                                wallpaperManager, wallpaperLauncher
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
                                if (!success) {
                                    isLoading.value = false
                                    wallpaperChanged = false
                                }
                            },
                            wallpaperManager, wallpaperLauncher
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
                    if (!success) {
                        isLoading.value = false
                        wallpaperChanged = false
                    }
                },
                wallpaperManager, wallpaperLauncher
            )

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

fun changeWallpaper2(
    context: Context,
    imageUrl: String,
    callback: (Boolean) -> Unit,
    wallpaperManager: WallpaperManager,
    launcher: ActivityResultLauncher<Intent>
) {
    // Ask the user where to set the wallpaper
    val options = arrayOf("Home Screen", "Lock Screen", "Both", "Crop")

    // Show dialog on the main thread
    CoroutineScope(Dispatchers.Main).launch {
        val selectedOption = suspendCoroutine<Int> { continuation ->
            AlertDialog.Builder(context)
                .setTitle("Set Wallpaper")
                .setItems(options) { _, which -> continuation.resume(which) }
                .setCancelable(false)
                .setNegativeButton("Cancel") { a, _ ->
                    a.dismiss()
                    callback(false)
                }
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


                    withContext(Dispatchers.Main) {
                        when (selectedOption) {
                            3 -> {
                                val imageUri = getUriFromUrl(context, imageUrl)
                                imageUri?.let { openWallpaperPicker(context, it, launcher) }
                                callback(false)

                            }

                            else -> {
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
                        }


                    }
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


suspend fun getUriFromUrl(context: Context, imageUrl: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
            val file = File(context.cacheDir, "temp_wallpaper.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun openWallpaperPicker(context: Context, imageUri: Uri) {
    val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
        setDataAndType(imageUri, "image/*")
        putExtra("mimeType", "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Set as Wallpaper"))
}

fun openWallpaperPicker(context: Context, imageUri: Uri, launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
        setDataAndType(imageUri, "image/*")
        putExtra("mimeType", "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    launcher.launch(Intent.createChooser(intent, "Set as Wallpaper"))
}