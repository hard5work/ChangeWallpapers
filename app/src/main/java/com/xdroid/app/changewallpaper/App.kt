package com.xdroid.app.changewallpaper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.xdroid.app.changewallpaper.di.module.appModule
import com.xdroid.app.changewallpaper.di.module.repoModule
import com.xdroid.app.changewallpaper.di.module.viewModelModule
import com.xdroid.app.changewallpaper.ui.adscreen.OpenApp.isAdAvailable
import com.xdroid.app.changewallpaper.ui.adscreen.OpenApp.loadAppOpenAd
import com.xdroid.app.changewallpaper.ui.adscreen.OpenApp.showAppOpenAdIfAvailable
import com.xdroid.app.changewallpaper.ui.adscreen.loadInterstitial
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.PreferenceHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.io.File
import java.util.Date

class App : Application(), Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
        baseApplication = this
        preferenceHelper = PreferenceHelper(this)
        MobileAds.initialize(this)
        loadInterstitial(this)
        loadAppOpenAd()
        registerActivityLifecycleCallbacks(this)
        startKoin() {
            androidLogger()
            androidContext(this@App)
            modules(appModule, repoModule, viewModelModule)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    companion object {
        lateinit var baseApplication: Context
        lateinit var preferenceHelper: PreferenceHelper

    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }

    fun loadAppOpenAd() {
        val adRequest = AdRequest.Builder().build()
        var countAd = preferenceHelper.getValue(PrefConstant.OPENAPPAD, 0) as Int

        DebugMode.e("Loading the appppppppppppp")
        if (countAd > 10)
            AppOpenAd.load(
                this, getString(R.string.openApp), adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        loadTime = Date().time
                        if (!isShowingAd) {

                            showAppOpenAdIfAvailable {
                                isShowingAd = false

                            }
                        }
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {

                        DebugMode.e("Data failed the appppppppppppp ${p0.message}")
                        // Handle the error
                    }
                }
            )
        countAd++
        preferenceHelper.setValue(PrefConstant.OPENAPPAD, countAd)

    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && (Date().time - loadTime) < 4 * 3600000 // 4 hours in milliseconds
    }

    fun showAppOpenAdIfAvailable(onAdDismissed: () -> Unit) {
        if (!isShowingAd && isAdAvailable()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
//                    loadAppOpenAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    isShowingAd = false
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            preferenceHelper.setValue(PrefConstant.OPENAPPAD, 0)
            appOpenAd?.show(currentActivity!!)
        } else {
            onAdDismissed()
        }
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
//        showAppOpenAdIfAvailable()
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = null
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

}