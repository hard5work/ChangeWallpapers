package com.xdroid.app.changewallpaper.ui.adscreen

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode


@Composable
fun BannerAdView() {

    val context = LocalContext.current
    val adUnitIds = context.getString(R.string.bannerId)
    //"ca-app-pub-3940256099942544/6300978111"
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // Add your adUnitID, this is for testing.
                adUnitId = adUnitIds
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}


var mInterstitialAd: InterstitialAd? = null

fun loadInterstitial(context: Context) {
    InterstitialAd.load(
        context,
        context.getString(R.string.interstital), //Change this with your own AdUnitID!
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null

                DebugMode.e("onAdFailedToLoad ${adError.message}")
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd

                DebugMode.e("Success Ful onAdLoaded")
            }
        }
    )
}

fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
    val activity = context as Activity
    DebugMode.e("isInit? $mInterstitialAd")
    if (mInterstitialAd == null){
        onAdDismissed()
    }
    if (mInterstitialAd != null && activity != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null
                DebugMode.e("Error Message ${e.message}")
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null

                DebugMode.e("Success Ful")
                loadInterstitial(context)
                onAdDismissed()
            }
        }
        mInterstitialAd?.show(activity)
    }
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
}
