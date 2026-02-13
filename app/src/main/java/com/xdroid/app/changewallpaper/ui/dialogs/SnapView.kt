package com.xdroid.app.changewallpaper.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.ui.adscreen.AdmobNativeAd
import com.xdroid.app.changewallpaper.ui.adscreen.ListBannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdState
import com.xdroid.app.changewallpaper.ui.components.ButtonComponent
import com.xdroid.app.changewallpaper.ui.components.OutlineButtonComponent
import com.xdroid.app.changewallpaper.ui.layouts.NativeAdContainer
import com.xdroid.app.changewallpaper.ui.layouts.ShimmerAdPlaceHolder
import com.xdroid.app.changewallpaper.ui.layouts.ShimmerAdPlaceHolder2
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ErrorSnackBar(
    message: String?,
    showDialog: Boolean, onDismiss: (Boolean) -> Unit
) {
    if (showDialog)
        Snackbar(
            action = {
                TextButton(onClick = { onDismiss(false) }) {
                    Text("Dismiss")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(message ?: "")
        }
}

@Composable
fun AlertDialogs(message: String, onButtonClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Alert") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = { onButtonClick() }) {
                Text("OK")

            }
        }
    )
}

@Composable
fun AlertDialogs(
    title: String = "Alert",
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { onButtonClick() }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun ErrorSnackbar(
    message: String?,
    durationMillis: Long = 3000
) {
    val snackbarVisibleState = remember { mutableStateOf(message != null) }
    val coroutineScope = rememberCoroutineScope()

    if (message != null) {
        LaunchedEffect(message) {
            coroutineScope.launch {
                delay(durationMillis)
                snackbarVisibleState.value = false
            }
        }
    }

    if (snackbarVisibleState.value) {
        Snackbar(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Red),
            contentColor = Color.White
        ) {
            Text(message ?: "")
        }
    }
}


@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Ok",
    dismissButtonText: String = "Cancel",
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
//                Image(
//                    painter = image,
//                    contentDescription = null,
//                )
//                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = message,
            )
        },
        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlineButtonComponent(
                        label = dismissButtonText,
                        onClick = { onDismissButtonClick() },
                        width = 120

                    )
                    ButtonComponent(
                        label = confirmButtonText,
                        onClick = { onConfirmButtonClick() },
                        width = 120

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .wrapContentWidth(align = Alignment.CenterHorizontally)


    )
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomAlertDialogImage(
    image: String,
    title: String = "Reward",
    message: String = "Enhance your device with this stunning wallpaper.",
    confirmButtonText: String = "Ok",
    dismissButtonText: String = "Cancel",
    onConfirmButtonClick: () -> Unit = {},
    onDismissButtonClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {

                Text(
                    text = message,
                )
                GlideImage(
                    model = image,
                    contentDescription = image,
                    loading = placeholder(R.drawable.shimmer_shape),
                    modifier = Modifier
                        .height(250.dp)
                        .width(300.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.FillHeight

                )

            }
        },
        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlineButtonComponent(
                        label = dismissButtonText,
                        onClick = { onDismissButtonClick() },
                        width = 120

                    )
                    ButtonComponent(
                        label = confirmButtonText,
                        onClick = { onConfirmButtonClick() },
                        width = 120

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .wrapContentWidth(align = Alignment.CenterHorizontally)


    )
}

@Composable
fun InfoAlertDialog(
    message: String,
    confirmButtonText: String = "Ok",
    onConfirmButtonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "App Info",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = message
            )
        },
        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonComponent(
                        label = confirmButtonText,
                        onClick = { onConfirmButtonClick() },
                        width = 120

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .wrapContentWidth(align = Alignment.CenterHorizontally)


    )
}

@Composable
fun LoadingAlertDialog(
) {
    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = true),

        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
//            .wrapContentWidth(align = Alignment.CenterHorizontally)


    )
}


@Composable
fun CustomAlertDialogWithAds(
    title: String,
    message: String,
    confirmButtonText: String = "Ok",
    dismissButtonText: String = "Cancel",
    dismissOnClickedOutside: Boolean = false,
    dismissOnBackPress: Boolean = false,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
) {
    val context = LocalContext.current
    /*val adUnitIds = rememberSaveable { context.getString(R.string.centerBanner) }
    val adView = remember {
        AdView(context).apply {
//            setAdSize(AdSize.LARGE_BANNER)
            val displayMetrics = context.resources.displayMetrics
            val density = displayMetrics.density
            val adWidthPixels = displayMetrics.widthPixels.toFloat()
            val adWidth = (adWidthPixels / density).toInt() - 100

            // Adaptive banner size
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)

            setAdSize(adSize)
            adUnitId = adUnitIds
        }
    }*/
    var nativeAd2 by remember { mutableStateOf<NativeAdState>(NativeAdState.Loading) }


    LaunchedEffect(Unit) {
        NativeAdManager.loadNativeAd(context) { ad ->
            nativeAd2 = ad
        }
    }
    // Keep track of the loading state
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var isAdError by rememberSaveable { mutableStateOf(false) }

   /* // Set the AdListener only once
    DisposableEffect(adView) {
        val adListener = object : AdListener() {
            override fun onAdLoaded() {
                DebugMode.e("Banner ad load success")
                isLoading = false
                isAdError = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                DebugMode.e("Banner ad load Failed-> ${error.message}")
                isLoading = false
                isAdError = true
            }
        }
        adView.adListener = adListener

        onDispose {
            adView.adListener = object : AdListener() {}
        }
    }*/

    var d0 by rememberSaveable { mutableStateOf(false) }
    var d1 by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        d0 = dismissOnClickedOutside
        d1 = dismissOnBackPress
    }


//    // Load the ad only once
//    LaunchedEffect(adView) {
//        adView.loadAd(AdRequest.Builder().build())
//    }

    AlertDialog(
        onDismissRequest = { onDismissButtonClick() },
        properties = DialogProperties(
            dismissOnClickOutside = d0,
            dismissOnBackPress = d1,
            usePlatformDefaultWidth = false
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
//                Image(
//                    painter = image,
//                    contentDescription = null,
//                )
//                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(
                    text = message,
                )
                Spacer(Modifier.height(10.dp))
//                ListBannerAdView(adView, isLoading, isAdError)
                NativeAdContainer(nativeAd2)

            }
        },
        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlineButtonComponent(
                        label = dismissButtonText,
                        onClick = { onDismissButtonClick() },
                        width = 180

                    )
                    ButtonComponent(
                        label = confirmButtonText,
                        onClick = { onConfirmButtonClick() },
                        width = 180

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
//        dismissButton = {
//            OutlineButtonComponent(
//                label = dismissButtonText,
//                onClick = { onDismissButtonClick() },
//                width = 120
//
//            )
//        },
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
//            .wrapContentWidth(align = Alignment.CenterHorizontally)


    )
}


@Composable
fun InfoAlertDialogWithAds(
    message: String,
    dismissOnClickedOutside: Boolean = false,
    dismissOnBackPress: Boolean = false,
    confirmButtonText: String = "Ok",
    onConfirmButtonClick: () -> Unit,
) {
    val context = LocalContext.current
//    val adUnitIds = rememberSaveable { context.getString(R.string.centerBanner) }
//    val adView = remember {
//        AdView(context).apply {
//            val displayMetrics = context.resources.displayMetrics
//            val density = displayMetrics.density
//            val adWidthPixels = displayMetrics.widthPixels.toFloat()
//            val adWidth = (adWidthPixels / density).toInt() -100
//
//            // Adaptive banner size
//            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
//
//            setAdSize(adSize)
////            setAdSize(AdSize.LARGE_BANNER)
//            adUnitId = adUnitIds
//        }
//    }
    var nativeAd2 by remember { mutableStateOf<NativeAdState>(NativeAdState.Loading) }


        LaunchedEffect(Unit) {
            NativeAdManager.loadNativeAd(context) { ad ->
                nativeAd2 = ad
            }
        }

    // Keep track of the loading state
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var isAdError by rememberSaveable { mutableStateOf(false) }

//    // Set the AdListener only once
//    DisposableEffect(adView) {
//        val adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                DebugMode.e("Banner ad load success")
//                isLoading = false
//                isAdError = false
//            }
//
//            override fun onAdFailedToLoad(error: LoadAdError) {
//                DebugMode.e("Banner ad load Failed-> ${error.message}")
//                isLoading = false
//                isAdError = true
//            }
//        }
//        adView.adListener = adListener
//
//        onDispose {
//            adView.adListener = object : AdListener() {}
//        }
//    }

    var d0 by rememberSaveable { mutableStateOf(false) }
    var d1 by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        d0 = dismissOnClickedOutside
        d1 = dismissOnBackPress
    }

//
//    // Load the ad only once
//    LaunchedEffect(adView) {
//        adView.loadAd(AdRequest.Builder().build())
//    }

    AlertDialog(
        onDismissRequest = { onConfirmButtonClick()},
        properties = DialogProperties(dismissOnClickOutside = d0, dismissOnBackPress = d1, usePlatformDefaultWidth = false),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "App Info",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = message
                )
                Spacer(modifier = Modifier.height(10.dp))
                Spacer(Modifier.height(10.dp))
//                ListBannerAdView(adView, isLoading, isAdError)
NativeAdContainer(nativeAd2)

            }
        },
        confirmButton = {
            Column() {

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonComponent(
                        label = confirmButtonText,
                        onClick = { onConfirmButtonClick() },
                        width = 180

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

        },
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)


    )
}


