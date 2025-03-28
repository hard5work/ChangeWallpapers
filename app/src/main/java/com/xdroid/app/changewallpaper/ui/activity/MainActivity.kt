package com.xdroid.app.changewallpaper.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chartboost.sdk.Chartboost.startWithAppId
import com.chartboost.sdk.callbacks.StartCallback
import com.chartboost.sdk.events.StartError
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.ui.adscreen.BannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.RewardedAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.mInterstitialAd
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstialAds
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.layouts.MyApp
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.ChangeWallpapersTheme
import com.xdroid.app.changewallpaper.ui.theme.backGroundColor
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.Locale


class MainActivity : ComponentActivity() {

    lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        OpenApp.showAppOpenAdIfAvailable(this)
        enableEdgeToEdge()


        App.preferenceHelper.setValue(PrefConstant.COUNTER, 0)
        // Start the 15-minute countdown timer
        if (mInterstitialAd == null)
            loadInterstitial(this)
        RewardedAdManager.loadAd(this)
        timer = object : CountDownTimer(5 * 60 * 1000, 1000) { // 15 minutes
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val time = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
//                DebugMode.e("Time Left $time")
            }

            override fun onFinish() {
                val dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Scrolling for too long")
                dialog.setMessage("Take a small break to continue.")
                dialog.setCancelable(false)
                dialog.setPositiveButton("View an ad") { dialog, _ ->
                    // Handle OK button click
                    if (mInterstitialAd == null) {
                        loadInterstitial(this@MainActivity) { _ ->
                            showInterstitial(this@MainActivity) {
                                timer.start()
                            }
                        }

                    } else {
                        showInterstitial(this@MainActivity) {
                            timer.start()
                        }
                    }
//                    showInterstialAds()
                    dialog.dismiss() // Close the dialog
                }
                dialog.show()


            }
        }

        timer.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }


        setContent {
            ChangeWallpapersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(background)
                ) {


                    Column(
                        modifier = Modifier.background(background),

                        ) {
                        AppUpdate()
//                        MainScreen()

                    }

                }
            }
        }


    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Handle case where permission is denied
            }
        }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val networkHelper: NetworkHelper = koinInject()
    val context = LocalContext.current

//    var showBanner by remember {
//        mutableStateOf(false)
//    }

//    val myScope = CoroutineScope(Dispatchers.IO + Job())
//    LaunchedEffect(Unit) {
//        myScope.launch {
//            delay(5000)
//            // Perform an action
//            showBanner = true
//            DebugMode.e("Loading for 5000 secconds.....")
//
//        }
//    }


// Cancel the scope when done

//    if (showBanner)


    Scaffold(modifier = Modifier.background(background)) { paddingValues ->


        Column(modifier = Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            {
                MyApp()

            }
            Text(
                text = "[${BuildConfig.VERSION_NAME}] (${BuildConfig.VERSION_CODE})",
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background)
                    .padding(vertical = 5.dp)
            )


            if (networkHelper.isNetworkConnected()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background)
                )
                {
                    BannerAdView()
//                    BannerAdView2()
                }
            }

////        if (showBanner)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
//            {
//
//                BannerAdView2()
////                BannerAdView3()
//
//            }
        }

    }
//    loadInterstitialAds()

}


@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as App
    var adShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        context.showAppOpenAdIfAvailable {
            DebugMode.e("Ad displayed")
            adShown = true // Update state when ad is dismissed
        }
    }

    // Once the ad is dismissed, navigate to MainScreen
    if (adShown) {
        LaunchedEffect(Unit) {
            delay(500) // Optional delay for smoother transition
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
    } else {
        // Show a loading indicator while the ad is loading
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    // Preview can be shown here
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}


@Composable
fun AppUpdate() {
    val context = LocalContext.current
    val activity = context as? Activity
    val appUpdateManager = remember { AppUpdateManagerFactory.create(context) }

    // Keep track of update status
    var isUpdateAvailable by remember { mutableStateOf(false) }
    var isUpdateDownloaded by remember { mutableStateOf(false) }
    var showAlert by rememberSaveable {
        mutableStateOf(false)
    }

    MainScreen()
    // Register for activity result to handle the update request
    val updateLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                // User canceled the update
            }
        }

    // Check for app updates
    LaunchedEffect(Unit) {
        //        DebugMode.e("is update available $isUpdateAvailable")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnFailureListener {

            DebugMode.e("is update available error  ${it.message}")
        }
        appUpdateInfoTask.addOnCompleteListener {
            DebugMode.e("is update available complete  ${it}")
        }


        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            DebugMode.e("is update available main $isUpdateAvailable")
            // Check if an update is available
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                isUpdateAvailable = true
                DebugMode.e("is update available 1 $isUpdateAvailable")

                // Request the update
                if (activity != null) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {

                DebugMode.e("is update available 4 $isUpdateAvailable")
            }

            // Listen for update download completion
            appUpdateManager.registerListener { state ->
                if (state.installStatus() == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED) {
                    isUpdateDownloaded = true
                }
            }
        }
    }

    if (isUpdateDownloaded) {
        showAlert = true
    }



    if (showAlert) {
        InfoAlertDialog(message = "App update available") {
            appUpdateManager.completeUpdate()
            showAlert = false
        }
    }
}



