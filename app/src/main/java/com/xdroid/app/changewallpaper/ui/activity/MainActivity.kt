package com.xdroid.app.changewallpaper.ui.activity

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.MobileAds
import com.xdroid.app.changewallpaper.ui.adscreen.BannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.ui.adscreen.removeInterstitial
import com.xdroid.app.changewallpaper.ui.layouts.MyApp
import com.xdroid.app.changewallpaper.ui.theme.ChangeWallpapersTheme
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val networkHelper:NetworkHelper by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        loadInterstitial(this)
        setContent {
            ChangeWallpapersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    Column {
                        if (networkHelper.isNetworkConnected()){
                            BannerAdView()
                        }
                        MyApp()


                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeInterstitial()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}