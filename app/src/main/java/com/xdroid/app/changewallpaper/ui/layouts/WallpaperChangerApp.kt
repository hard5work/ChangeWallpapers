package com.xdroid.app.changewallpaper.ui.layouts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.chartboost.sdk.impl.wa
import com.google.android.gms.ads.nativead.NativeAd
import com.skydoves.landscapist.ImageOptions
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.ui.adscreen.AdmobNativeAd
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdState
import com.xdroid.app.changewallpaper.ui.adscreen.RewardedAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.getScreenHeight
import com.xdroid.app.changewallpaper.ui.components.getScreenWidth
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.vm.FavoriteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperChangerApp(navController: NavController, imageUrl: String) {

    val networkHelper: NetworkHelper = koinInject()
    val favoriteViewModel: FavoriteViewModel = koinViewModel()
    val isFavorite by favoriteViewModel.singleItem.collectAsState()
    val notes by favoriteViewModel.notes.collectAsState()

    var downloadImage by rememberSaveable { mutableStateOf(false) }
    var showAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }
    var wallpaperChanged by rememberSaveable { mutableStateOf(false) }
    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
    var buttonClicked by rememberSaveable { mutableStateOf(false) }
    val isLoading = rememberSaveable { mutableStateOf(false) }
    var isDataLoaded by rememberSaveable { mutableStateOf(false) }
    var isFav by rememberSaveable { mutableStateOf(false) }
    var shareImage by rememberSaveable { mutableStateOf(false) }
    var showAdInfo  by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit){
        favoriteViewModel.loadSingleItem(imageUrl)
    }
    fun checkFav() {
//        favoriteViewModel.loadSingleItem(imageUrl)
        isFav = isFavorite != null

        DebugMode.e("IS Fav = $isFav ${notes.size}")

    }

    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAdState>(NativeAdState.Loading) }
    var counter by remember {
        mutableIntStateOf(App.preferenceHelper.getValue(PrefConstant.COUNTER, 0) as Int)
    }

    var favour by remember {
        mutableIntStateOf(App.preferenceHelper.getValue(PrefConstant.Favorite, 0) as Int)
    }

    var wallpaperCounter by remember {
        mutableIntStateOf(App.preferenceHelper.getValue(PrefConstant.Wallpaper, 0) as Int)
    }

    // Load the ad when the screen appears
    LaunchedEffect(Unit) {
        NativeAdManager.loadNativeAd(context) { ad ->
            nativeAd = ad
        }

    }

    LaunchedEffect(isFavorite) {
        checkFav()
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

    if (downloadImage) {
        LaunchedEffect(Unit) {

            downloadImage(
                context = context,
                url = imageUrl,
                action = {
                    downloadImage = false
                }
            )


        }
    }

    if (showAdInfo){
        CustomAlertDialogWithAds(title = "Watch AD", message = "Watch ad to download image?", onConfirmButtonClick =  {
            showAdInfo =  false
            if (networkHelper.isNetworkConnected()) {

                (context as Activity).let {
                    RewardedAdManager.showAd(it, onRewardEarned =  { _ ->

                        downloadImage = true
                    }, status = { it->
                        showAlert = !it
                        alertMessage ="Ad not ready"

                    })}
//            }
            }else{
                downloadImage = false
                showAlert = true
                alertMessage = "No internet connection"
            }
        },
            confirmButtonText = "Watch Ad", dismissButtonText = "Cancel", onDismissButtonClick = {
                showAdInfo= false
            }
        )
    }
    if(showAlert){
        InfoAlertDialogWithAds(alertMessage) {
            showAlert = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
//
//        GlideImage(
//            model = imageUrl,
//            contentDescription = "Image",
//            modifier = Modifier
//                .fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
        com.skydoves.landscapist.glide.GlideImage(
            imageModel = { imageUrl },
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    FullScreenShimmer()

                }
            },
            failure = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    Text("Image failed to load.")

                }
            },

            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            imageOptions = ImageOptions(contentScale = ContentScale.Crop)
        )

        IconButton(
            onClick = {
                backHandle()
            },

            modifier = Modifier.padding(8.dp) .background(
                color = Color.White, // change to your color
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                tint = black,
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
            if (!isLoading.value) {
                Button(
                    onClick = {
                        buttonClicked = true
                        isLoading.value = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(3.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Wallpaper,
                            contentDescription = "Change Icons",
                            tint = black,
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Set Wallpaper", color = black, fontSize = 14.sp)
                    }
                }
            } else {
                CircularProgressIndicator(color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                /*  if (!isLoading.value) {
                      Button(
                          onClick = {
                              buttonClicked = true
                              isLoading.value = true
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                          elevation = ButtonDefaults.elevatedButtonElevation(3.dp),
                          modifier = Modifier
                              .height(50.dp)
                              .padding(vertical = 5.dp)
                              .clip(RoundedCornerShape(10.dp))
                      ) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Center
                          ) {

                              Icon(
                                  imageVector = Icons.Filled.Wallpaper,
                                  contentDescription = "Change Icons",
                                  tint = black,
                              )
                          }
                      }
                  } else {
                      CircularProgressIndicator(color = Color.White)
                  }
                  Spacer(Modifier.width(25.dp))*/

                Button(
                    onClick = {
                        if (favour >= 5) {
                            if (mInterstitialAd == null) {
                                loadInterstitial(context) { _ ->
                                    showInterstitial(context) {
                                        if (isFav) {
                                            if (isFavorite != null)
                                                favoriteViewModel.deleteNote(isFavorite!!)
                                        } else {
                                            favoriteViewModel.addNote(content = imageUrl)
                                        }
                                        isFav = !isFav
                                    }
                                }

                            } else {
                                showInterstitial(context) {
                                    if (isFav) {
                                        if (isFavorite != null)
                                            favoriteViewModel.deleteNote(isFavorite!!)
                                    } else {
                                        favoriteViewModel.addNote(content = imageUrl)
                                    }
                                    isFav = !isFav
                                }
                            }
                            favour = 0

                        } else {
                            if (isFav) {
                                if (isFavorite != null)
                                    favoriteViewModel.deleteNote(isFavorite!!)
                            } else {
                                favoriteViewModel.addNote(content = imageUrl)
                            }
                            isFav = !isFav
                        }

                        favour++
                        App.preferenceHelper.setValue(PrefConstant.Favorite, favour)

//                    checkFav()

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(3.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Change",
                            tint = black
                        )
                    }
                }
                Spacer(Modifier.width(25.dp))

                Box {
                    Button(
                        onClick = {

                            showAdInfo = true

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.elevatedButtonElevation(3.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Download",
                                tint = black
                            )
                        }
                    }

                    Text("AD", modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), color = black, fontSize = 12.sp)
                }


                Spacer(Modifier.width(25.dp))
                Box {
                    Button(
                        onClick = {

                            if (networkHelper.isNetworkConnected()) {
                                shareImage = true
//            }
                            }else{
                                downloadImage = false
                                showAlert = true
                                alertMessage = "No internet connection"
                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.elevatedButtonElevation(3.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = black
                            )
                        }
                    }

                    Text("AD", modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), color = black, fontSize = 12.sp)
                }


            }



            Spacer(modifier = Modifier.height(16.dp))

        }
    }

    var infoMessage by rememberSaveable { mutableStateOf("Wallpaper changed successfully") }
    if (wallpaperChanged) {
        InfoAlertDialogWithAds(
            message = infoMessage,
            dismissOnBackPress = true,
            dismissOnClickedOutside = true
        ) {
            isLoading.value = false
            wallpaperChanged = false
            removeInterstitial()
            infoMessage = "Wallpaper changed successfully"

        }
    }

    if (buttonClicked) {
        if (wallpaperCounter > 3) {
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
                                        wallpaperCounter = 0
                                        App.preferenceHelper.setValue(PrefConstant.Wallpaper,wallpaperCounter)

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
                                    wallpaperCounter = 0
                                    App.preferenceHelper.setValue(PrefConstant.Wallpaper,wallpaperCounter)

                                },
                                wallpaperManager, wallpaperLauncher
                            )
                        }
                    }
                }

            } else {
                buttonClicked = false
                wallpaperChanged = true
                infoMessage ="No Internet Connection"
//                changeWallpaper2(
//                    context = context,
//                    imageUrl, { success ->
//                        wallpaperChanged = success
//                        if (!success) {
//                            isLoading.value = false
//                            wallpaperChanged = false
//                        }
//                    },
//                    wallpaperManager, wallpaperLauncher
//                )

            }
        }else{
            buttonClicked = false
            if (networkHelper.isNetworkConnected()) {
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
            else{
                buttonClicked = false
                wallpaperChanged = true
                infoMessage ="No Internet Connection"
            }
            wallpaperCounter++
            App.preferenceHelper.setValue(PrefConstant.Wallpaper,wallpaperCounter)

        }

    }


    if (shareImage) {
        if (networkHelper.isNetworkConnected()) {
            LaunchedEffect(Unit) {
                if (mInterstitialAd == null) {
                    loadInterstitial(context) { _ ->
                        showInterstitial(context) {
                            shareImage = false
                            shareImage(context, imageUrl)
                        }
                    }

                } else {
                    showInterstitial(context) {
                        shareImage = false
                        shareImage(context, imageUrl)
                    }
                }
            }

        } else {
            shareImage = false
            showAlert = true
            alertMessage ="No Internet Connection"

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


const val PERMISSION_REQUEST_CODE = 1001

fun checkAndRequestPermission(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
        false
    } else {
        true
    }
}

suspend fun downloadImage(
    context: Context,
    url: String,
    fileName: String = "wallpaper_${System.currentTimeMillis()}.jpg",
    action: (Boolean) -> Unit = {}
) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Failed to download image")
            }

            val inputStream = response.body?.byteStream() ?: throw Exception("No stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped Storage - Android 10+
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/Wallpapers"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri: Uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw Exception("Failed to create MediaStore entry")

                resolver.openOutputStream(uri)?.use { output ->
                    inputStream.copyTo(output)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

            } else {
                // Legacy storage - Android 9 and below
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val wallpapersDir = File(picturesDir, "Wallpapers")
                if (!wallpapersDir.exists()) wallpapersDir.mkdirs()

                val file = File(wallpapersDir, fileName)
                val outputStream: OutputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.close()

                // Make image show up in gallery
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )
            }

            inputStream.close()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                action(true)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                action(false)
            }
        }
    }
}


fun shareImage(context: Context, imageUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Download the image using Glide
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()

            // Save to cache
            val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val file = File(cacheDir, "shared_image.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, out)
            }

            // Get URI from FileProvider
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to share image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}