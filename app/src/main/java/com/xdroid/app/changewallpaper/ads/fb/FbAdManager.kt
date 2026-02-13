package com.xdroid.app.changewallpaper.ads.fb

import android.content.Context
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener
import com.xdroid.app.changewallpaper.ads.fb.FacebookAdsManager.rewarded
import com.xdroid.app.changewallpaper.data.AdsConstants

object FacebookAdsManager {

    private var interstitial: com.facebook.ads.InterstitialAd? = null
    private var rewarded: RewardedVideoAd? = null

    fun banner(context: Context): com.facebook.ads.AdView {
        return com.facebook.ads.AdView(
            context,
            AdsConstants.FB_BANNER,
            com.facebook.ads.AdSize.BANNER_HEIGHT_50
        ).apply { loadAd() }
    }

    fun loadInterstitial(context: Context) {
        interstitial = com.facebook.ads.InterstitialAd(
            context,
            AdsConstants.FB_INTERSTITIAL
        )
        interstitial?.loadAd()
    }

    fun showInterstitial(): Boolean {
        return if (interstitial?.isAdLoaded == true) {
            interstitial?.show()
            true
        } else false
    }

    fun loadRewarded(context: Context) {
        rewarded = RewardedVideoAd(context, AdsConstants.FB_REWARDED)
        rewarded?.loadAd()
    }

    fun showRewarded(onReward: () -> Unit): Boolean {
        return if (rewarded?.isAdLoaded == true) {
//            rewarded?.show(object : RewardedVideoAdListener {
//                override fun onRewardedVideoCompleted() = onReward()
//                override fun onRewardedVideoClosed() {}
//                override fun onError(adError: AdError?) {}
//                override fun onAdLoaded(ad: Ad?) {}
//                override fun onAdClicked(ad: Ad?) {}
//                override fun onLoggingImpression(ad: Ad?) {}
//            })
            true
        } else false
    }
}
