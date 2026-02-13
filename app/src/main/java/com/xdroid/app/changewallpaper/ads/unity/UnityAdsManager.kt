package com.xdroid.app.changewallpaper.ads.unity

import android.app.Activity
import com.unity3d.ads.UnityAds
import com.xdroid.app.changewallpaper.data.AdsConstants

//object UnityAdsManager {
//
//    fun showInterstitial(activity: Activity): Boolean {
//        return if (UnityAds.isReady(AdsConstants.UNITY_INTERSTITIAL)) {
//            UnityAds.show(activity, AdsConstants.UNITY_INTERSTITIAL)
//            true
//        } else false
//    }
//
//    fun showRewarded(activity: Activity, onReward: () -> Unit): Boolean {
//        return if (UnityAds.isReady(AdsConstants.UNITY_REWARDED)) {
//            UnityAds.show(activity, AdsConstants.UNITY_REWARDED,
//                object : IUnityAdsListener {
//                    override fun onUnityAdsFinish(
//                        placementId: String?,
//                        state: UnityAds.FinishState?
//                    ) {
//                        if (state == UnityAds.FinishState.COMPLETED) onReward()
//                    }
//                    override fun onUnityAdsReady(p0: String?) {}
//                    override fun onUnityAdsStart(p0: String?) {}
//                    override fun onUnityAdsError(p0: UnityAds.UnityAdsError?, p1: String?) {}
//                })
//            true
//        } else false
//    }
//}
