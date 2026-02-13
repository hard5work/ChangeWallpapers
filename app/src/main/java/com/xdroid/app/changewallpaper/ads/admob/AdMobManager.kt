package com.xdroid.app.changewallpaper.ads.admob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.xdroid.app.changewallpaper.data.AdsConstants

object AdMobManager {

    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null

    // Banner
    fun banner(context: Context): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = AdsConstants.ADMOB_BANNER
            loadAd(AdRequest.Builder().build())
        }
    }

    // Interstitial
    fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            AdsConstants.ADMOB_INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity): Boolean {
        return if (interstitial != null) {
            interstitial?.show(activity)
            true
        } else false
    }

    // Rewarded
    fun loadRewarded(context: Context) {
        RewardedAd.load(
            context,
            AdsConstants.ADMOB_REWARDED,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewarded = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewarded = null
                }
            }
        )
    }

    fun showRewarded(activity: Activity, onReward: () -> Unit): Boolean {
        return if (rewarded != null) {
            rewarded?.show(activity) { onReward() }
            true
        } else false
    }
}
