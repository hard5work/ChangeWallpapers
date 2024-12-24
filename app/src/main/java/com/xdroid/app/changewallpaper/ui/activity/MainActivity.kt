package com.xdroid.app.changewallpaper.ui.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.ui.adscreen.BannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.ListBannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.layouts.MyApp
import com.xdroid.app.changewallpaper.ui.layouts.ShimmerPlaceholder
import com.xdroid.app.changewallpaper.ui.theme.ChangeWallpapersTheme
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import java.sql.Time
import java.util.Locale

class MainActivity : ComponentActivity() {

    lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        OpenApp.showAppOpenAdIfAvailable(this)
        enableEdgeToEdge()

        App.preferenceHelper.setValue(PrefConstant.COUNTER, 0)
        // Start the 15-minute countdown timer
        loadInterstitial(this)
//        timer = object : CountDownTimer(15 * 60 * 1000, 1000) { // 15 minutes
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                val time = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
//                DebugMode.e("Time Left $time")
//            }
//
//            override fun onFinish() {
//                showInterstitial(this@MainActivity) {
//                    timer.start()
//                }
//            }
//        }
//        timer.start()

        setContent {
            ChangeWallpapersTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = background,
                ) { padding ->

                    Column(modifier = Modifier.padding(padding)) {
                        AppUpdate()
//                        MainScreen()

                    }
                }
            }
        }


    }


}


@Composable
fun MainScreen() {
    val networkHelper: NetworkHelper = koinInject()
    Column() {
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
                .padding(vertical = 5.dp)
        )


        if (networkHelper.isNetworkConnected()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                BannerAdView()
            }
        }
    }
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
    MainScreen()


    if (showAlert) {
        InfoAlertDialog(message = "App update available") {
            appUpdateManager.completeUpdate()
            showAlert = false
        }
    }
}



