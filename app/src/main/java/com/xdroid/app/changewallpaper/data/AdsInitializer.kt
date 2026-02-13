package com.xdroid.app.changewallpaper.data

import android.app.Activity
import android.content.Context
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.unity3d.ads.UnityAds

object AdsInitializer {

    fun init(context: Context) {
        // Google
        MobileAds.initialize(context)

        // Facebook
        AudienceNetworkAds.initialize(context)

        // Unity
        UnityAds.initialize(
            context as Activity,
            AdsConstants.UNITY_GAME_ID,
            false
        )
    }
}
