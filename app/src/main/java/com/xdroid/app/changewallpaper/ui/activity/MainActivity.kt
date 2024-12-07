package com.xdroid.app.changewallpaper.ui.activity

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.ui.adscreen.BannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.CheckForAppUpdate
import com.xdroid.app.changewallpaper.ui.adscreen.OpenApp
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.layouts.MyApp
import com.xdroid.app.changewallpaper.ui.theme.ChangeWallpapersTheme
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import java.sql.Time
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val networkHelper: NetworkHelper by inject()

    lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        OpenApp.showAppOpenAdIfAvailable(this)
        App.preferenceHelper.setValue(PrefConstant.COUNTER, 0)
        // Start the 15-minute countdown timer
        loadInterstitial(this)
        timer = object : CountDownTimer(15 * 60 * 1000, 1000) { // 15 minutes
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val time = String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)
                DebugMode.e("Time Left $time")
            }

            override fun onFinish() {
                showInterstitial(this@MainActivity) {
                    timer.start()
                }
            }
        }
        timer.start()

        setContent {
            ChangeWallpapersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {

                    Column {

                        CheckForAppUpdate(
                            onUpdateFailed = { exception ->
                                // Handle update failure here (e.g., show a message to the user)
                            },
                            onUpdateComplete = {
                                // Handle post-update completion here (e.g., refresh content)
                            }
                        )
                        MainScreen()
//                        val navController = rememberNavController()
//                        NavHost(navController, startDestination = "splash") {
//                            composable("splash") { SplashScreen(navController) }
//                            composable("main") { MainScreen() }
//                        }
//                        Column(modifier = Modifier.weight(1f)) {
//                            MyApp()
//                        }
//                        if (networkHelper.isNetworkConnected()){
//                            BannerAdView()
//                        }

                    }
                }
            }
        }


    }


    @Composable
    private fun MainScreen() {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            {
                MyApp()

            }


            if (networkHelper.isNetworkConnected()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    BannerAdView()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeInterstitial()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
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



