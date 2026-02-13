package com.xdroid.app.changewallpaper.ui.adscreen

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.setPadding
import com.chartboost.sdk.ads.Ad
import com.chartboost.sdk.ads.Banner
import com.chartboost.sdk.ads.Interstitial
import com.chartboost.sdk.callbacks.BannerCallback
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.events.CacheError
import com.chartboost.sdk.events.CacheEvent
import com.chartboost.sdk.events.ClickError
import com.chartboost.sdk.events.ClickEvent
import com.chartboost.sdk.events.DismissEvent
import com.chartboost.sdk.events.ImpressionEvent
import com.chartboost.sdk.events.ShowError
import com.chartboost.sdk.events.ShowEvent
import com.chartboost.sdk.impl.ad
import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.ui.activity.MainActivity
import com.xdroid.app.changewallpaper.ui.layouts.AdSection
import com.xdroid.app.changewallpaper.ui.layouts.ShimmerAdPlaceHolder
import com.xdroid.app.changewallpaper.ui.theme.backGroundColor
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.colorPrimary
import com.xdroid.app.changewallpaper.ui.theme.shimmerColor3
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date


@Composable
fun BannerAdView() {

    val context = LocalContext.current
    val adUnitIds = context.getString(R.string.bannerId)
    //"ca-app-pub-3940256099942544/6300978111"
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
//                setAdSize(AdSize.BANNER)
                val displayMetrics = ctx.resources.displayMetrics
                val density = displayMetrics.density
                val adWidthPixels = displayMetrics.widthPixels.toFloat()
                val adWidth = (adWidthPixels / density).toInt()

                // Adaptive banner size
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth)

                setAdSize(adSize)
                // Add your adUnitID, this
                // is for testing.
                adUnitId = adUnitIds
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}


@Composable
fun ListBannerAdView(adView: AdView, isLoading: Boolean, isAdError: Boolean) {

    val context = LocalContext.current
    val adUnitIds = context.getString(R.string.centerBanner)

//    var isLoading by rememberSaveable { mutableStateOf(true) }
    // Set the size of the ad container (100x100 dp)
    // UI Composition

    var isLoading2 by rememberSaveable { mutableStateOf(false) }
    Column {
        if (isLoading) {
            ShimmerAdPlaceHolder()
        }
        if (isAdError) {
            isLoading2 = true
//            Box(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .width(320.dp)
//                    .height(100.dp)
//                    .clip(
//                        RoundedCornerShape(16.dp)
//                    )
//                    .background(shimmerColor3)
//            ) {
//
//                Text(
//                    text = "",
//                    color = Color.Black,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
        } else {
            isLoading2 = false
        }

        AndroidView(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            factory = { adView },
            update = { adView ->
                adView.loadAd(AdRequest.Builder().build())
                // Update logic if needed (e.g., reloading the ad)
            }
        )
    }
    /*
        if (isAdError)
            Column {
                if (isLoading2)
                    ShimmerAdPlaceHolder()

                AndroidView(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    factory = { context ->
                        AdView(context).apply {
    //                setAdSize(adSize)
                            setAdSize(AdSize.LARGE_BANNER)
                            adUnitId = adUnitIds
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isLoading2 = false
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    isLoading2 = false
                                }
                            }

                            loadAd(AdRequest.Builder().build())
                        }
                    },
                    update = { adView ->
                        adView.loadAd(AdRequest.Builder().build())
                    }
                )

            }*/
}


var mInterstitialAd: InterstitialAd? = null

fun loadInterstitial(context: Context, onDataLoaded: (Boolean) -> Unit = {}) {
    InterstitialAd.load(
        context,
        context.getString(R.string.interstital), //Change this with your own AdUnitID!
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onDataLoaded(false)
                DebugMode.e("onAdFailedToLoad  from loadInterstitialAd ->${adError.message}")
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                onDataLoaded(true)
                DebugMode.e("Success Ful onAdLoaded from loadInterstitialAd")
            }
        }
    )
}

fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
    val activity = context as Activity
    DebugMode.e("isInit? $mInterstitialAd")
    if (mInterstitialAd == null) {
        onAdDismissed()
    }
    if (mInterstitialAd != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null
                DebugMode.e("Error Message -> showInterstitial -> ${e.message}")
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                DebugMode.e("Success Ful -> showInterstitial ")
                loadInterstitial(context)
                onAdDismissed()
            }
        }
        mInterstitialAd?.show(activity)
    }
}

fun removeInterstitial() {
    if (mInterstitialAd != null) {
        mInterstitialAd!!.fullScreenContentCallback = null
        mInterstitialAd = null
    }
}

object OpenApp {
    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var loadTime: Long = 0

    fun loadAppOpenAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context, context.getString(R.string.openApp), adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    // Handle the error
                }
            }
        )
    }

    fun isAdAvailable(): Boolean {
        return appOpenAd != null && (Date().time - loadTime) < 4 * 3600000 // 4 hours in milliseconds
    }

    fun showAppOpenAdIfAvailable(context: Context) {
        if (!isShowingAd && isAdAvailable()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    loadAppOpenAd(context)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    isShowingAd = false
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            appOpenAd?.show(MainActivity())

        } else {
            loadAppOpenAd(context)
        }
    }


}


@Composable
fun BannerAdView2() {
    //"ca-app-pub-3940256099942544/6300978111"

    var adLoaded by remember {
        mutableStateOf(0)
    }

    // State to hold the banner view
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { context ->
            // Create the FrameLayout for the banner
            val banner = bannerChartBoost(context, onDataLoaded = { it ->
                DebugMode.e("Data $adLoaded running ")
                adLoaded++

            })
            DebugMode.e("Data ${banner.getBannerWidth()}  and height ${banner.getBannerHeight()}")
//            if (adLoaded)
            banner.cache()
            banner // Return the banner view
        },
        update = {
            if (adLoaded == 1) {
                it.show()
            }
        }

    )
}

//
//@Composable
//fun BannerAdView3() {
//    //"ca-app-pub-3940256099942544/6300978111"
//
//    var adLoaded by rememberSaveable {
//        mutableStateOf(false)
//    }
//
//    var bannerView: Banner? by remember {
//        mutableStateOf(null)
//    }
//
//    // State to hold the banner view
//    AndroidView(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(80.dp)
//            .background(Color.Green),
//        factory = { context ->
//            // Create the FrameLayout for the banner
//            val banner = bannerChartBoost(context, object :
//                BannerCallback {
//
//                override fun onAdLoaded(event: CacheEvent, cacheError: CacheError?) {
//                    DebugMode.e("on Adloaded>>>>><<<<<<< ${cacheError?.exception}")
//                    DebugMode.e("on Adloaded >>>><<<<<< ${event.adID}")
//                    adLoaded = true
//
//
//                }
//
//                override fun onAdRequestedToShow(event: ShowEvent) {
//                    DebugMode.e("on onAdRequestedToShow ${event?.ad}")
//
//                }
//
//                override fun onAdShown(event: ShowEvent, error: ShowError?) {
//                    DebugMode.e("on onAdShown ${event?.adID}")
//                    adLoaded = true
//
//
//                }
//
//                override fun onAdClicked(event: ClickEvent, error: ClickError?) {
//                    DebugMode.e("on onAdClicked ${event?.ad}")
//
//                }
//
//                override fun onImpressionRecorded(event: ImpressionEvent) {
//                    DebugMode.e("on onImpressionRecorded ${event?.ad}")
//
//                }
//            }).apply {
//                // Set layout parameters for FrameLayout
//                layoutParams = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT, // Width to match parent
//                    1000 // Height wraps content
//                )
//
//            }
//            banner.show()
//            bannerView = banner
//            banner // Return the banner view
//        },
//        update = {
//            if (adLoaded)
//                bannerView?.show()
//        }
//
//    )
//}

var interstitial: Interstitial? = null


fun showInterstialAds() {

    interstitial?.show()
}
//
//fun loadInterstitialAds() {
//
//    interstitial = interstitialChartBoost()
//    interstitial?.cache()
////    var showBanner by remember {
////        mutableStateOf(false)
////    }
//    /*
//
//        val myScope = CoroutineScope(Dispatchers.IO + Job())
//        LaunchedEffect(Unit) {
//            myScope.launch {
//                delay(5000)
//                // Perform an action
//                showBanner = true
//                DebugMode.e("Loading for 5000 secconds.....")
//
//            }
//        }
//    */
//
//}


fun bannerChartBoost(context: Context, onDataLoaded: (Boolean) -> Unit) =
    Banner(context, context.getString(R.string.bannerLocation), Banner.BannerSize.STANDARD, object :
        BannerCallback {

        override fun onAdLoaded(event: CacheEvent, cacheError: CacheError?) {
            DebugMode.e("on Adloaded  exception${cacheError?.code}")
            DebugMode.e("on Adloaded adId <><><><><><><><><><<  ${event.adID}")
            if (cacheError != null) {

                DebugMode.e("on Adloaded Error ${cacheError.code}")
            } else {

                DebugMode.e("on Adloaded success with ID <<>><><><><>< ${event.adID}")
                onDataLoaded(true)
            }

        }

        override fun onAdRequestedToShow(event: ShowEvent) {
            DebugMode.e("on onAdRequestedToShow ${event?.ad}")
            onDataLoaded(false)
        }

        override fun onAdShown(event: ShowEvent, error: ShowError?) {
            DebugMode.e("on onAdShown location ${event.ad.location}")
            DebugMode.e("on onAdShown id <<><><><><><><><><> ${event.adID}")
            DebugMode.e("on onAdShown  Error ${error?.code}")
//            onDataLoaded(true)


        }

        override fun onAdClicked(event: ClickEvent, error: ClickError?) {
            DebugMode.e("on onAdClicked ${event?.ad}")

        }

        override fun onImpressionRecorded(event: ImpressionEvent) {
            DebugMode.e("on onImpressionRecorded ${event?.ad}")

        }
    }, null)
//
//fun bannerChartBoost(context: Context, listener: BannerCallback) =
//    Banner(
//        context,
//        context.getString(R.string.bannerLocation),
//        Banner.BannerSize.STANDARD,
//        listener,
//        null
//    )

//
//fun interstitialChartBoost() =
//    Interstitial("intersitialAnime", object : InterstitialCallback {
//
//        override fun onAdDismiss(event: DismissEvent) {
//            DebugMode.e("on interstitialChartBoost onAdDismiss ${event?.ad}")
//
//        }
//
//        override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
//            DebugMode.e("on interstitialChartBoost onAdLoaded ${event?.ad}")
//            DebugMode.e("on interstitialChartBoost onAdLoaded Error ${error?.code}")
//
//
//            // after this is successful ad can be shown
//        }
//
//
//        override fun onAdRequestedToShow(event: ShowEvent) {
//            DebugMode.e("on interstitialChartBoost onAdRequestedToShow ${event?.ad}")
//
//        }
//
//        override fun onAdShown(event: ShowEvent, showError: ShowError?) {
//            DebugMode.e("on interstitialChartBoost onAdShown ${event?.ad.toString()}")
//            DebugMode.e("on interstitialChartBoost onAdShown ${showError?.code.toString()}")
//
//
//        }
//
//        override fun onAdClicked(event: ClickEvent, clickError: ClickError?) {
//            DebugMode.e("on interstitialChartBoost onAdClicked ${event?.ad}")
//
//        }
//
//        override fun onImpressionRecorded(event: ImpressionEvent) {
//            DebugMode.e("on interstitialChartBoost onImpressionRecorded ${event?.ad}")
//
//        }
//    }, null)


object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null

    fun loadAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            context.getString(R.string.rewardAds),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("RewardedAd", "Failed to load ad: ${adError.message}")
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("RewardedAd", "Ad successfully loaded")
                    rewardedAd = ad
                }
            })
    }

    fun showAd(activity: Activity, onRewardEarned: (RewardItem) -> Unit, status:(Boolean)-> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("RewardedAd", "Ad was dismissed.")
                    rewardedAd = null
                    loadAd(activity) // Reload ad for future use
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e("RewardedAd", "Ad failed to show: ${adError.message}")
                    rewardedAd = null
                    status(false)
                }
            }

            rewardedAd?.show(activity) { rewardItem ->
                Log.d("RewardedAd", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned(rewardItem)
                status(true)
            }
        } else {
            Log.e("RewardedAd", "Ad not ready")
            status(false)
            loadAd(context = activity)
        }
    }
}
//
//object NativeAdManager {
//    private var nativeAd: NativeAd? = null
////    private const// Test Ad ID
//
//
//    fun loadNativeAd(context: Context, onAdLoaded: (NativeAd?) -> Unit) {
//        val AD_UNIT_ID = context.getString(R.string.nativeAds)
//        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
//            .forNativeAd { ad ->
//                Log.d("NativeAd", "Native Ad Loaded")
//                nativeAd = ad
//                onAdLoaded(ad)
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.e("NativeAd", "Failed to load native ad: ${adError.message}")
//                    onAdLoaded(null)
//                }
//            })
//            .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//    }
//}

sealed class NativeAdState {
    object Loading : NativeAdState()
    data class Loaded(val ad: NativeAd) : NativeAdState()
    object Failed : NativeAdState()
}

object NativeAdManager {

    private var currentNativeAd: NativeAd? = null
    private var isLoading = false

    fun loadNativeAd(
        context: Context,
//        onAdLoaded: (NativeAd?) -> Unit,
        onStateChanged: (NativeAdState) -> Unit
    ) {
        if (isLoading) return

        isLoading = true

        onStateChanged(NativeAdState.Loading)

        val adUnitId = context.getString(R.string.nativeAds)

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { newAd ->

                // Destroy previous ad to prevent memory leak
                currentNativeAd?.destroy()

                currentNativeAd = newAd
                isLoading = false

                Log.d("NativeAd", "Native Ad Loaded")
//                onAdLoaded(newAd)
                onStateChanged(NativeAdState.Loaded(newAd))
            }
            .withAdListener(object : AdListener() {

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false

                    Log.e(
                        "NativeAd",
                        "Failed to load native ad: ${error.code} - ${error.message}"
                    )

                    currentNativeAd = null
//                    onAdLoaded(null)
                    onStateChanged(NativeAdState.Failed)
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun destroy() {
        currentNativeAd?.destroy()
        currentNativeAd = null
    }
}


@Composable
fun AdmobNativeAd(nativeAd: NativeAd?) {
    val context = LocalContext.current

    nativeAd?.let { ad ->
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            factory = { ctx ->
                val adView = NativeAdView(ctx)
                val linearLayout = LinearLayout(ctx)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                linearLayout.setPadding(10)

                val adText = TextView(ctx)
                adText.setTextColor(white.toArgb())
                adText.setPadding(10)
                adText.setBackgroundColor(black.toArgb())
                adText.text = "Ad"
                adText.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                linearLayout.addView(adText)

                val mediaView = MediaView(ctx)
//                mediaView.id = 2
                mediaView.layoutParams =
                    LinearLayout.LayoutParams(MATCH_PARENT, 550.dp.value.toInt())
                linearLayout.addView(mediaView)


                val iconView = ImageView(ctx)
                iconView.setPadding(10)
//                iconView.id = 5
                linearLayout.addView(iconView)
                val horizontalView = LinearLayout(ctx)
                horizontalView.orientation = LinearLayout.HORIZONTAL
                horizontalView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                horizontalView.setPadding(10)
                horizontalView.gravity = Gravity.CENTER_VERTICAL


                val contentView = LinearLayout(ctx)
                contentView.orientation = LinearLayout.VERTICAL
                contentView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                contentView.setPadding(10)


                val headlineView = TextView(ctx)
                headlineView.setTextColor(white.toArgb())
                headlineView.textSize = 24f
                headlineView.setPadding(10)
//                headlineView.id = 1
                contentView.addView(headlineView)

                val bodyView = TextView(ctx)
                bodyView.setTextColor(white.toArgb())
                bodyView.setPadding(10)
//                bodyView.id = 3
                contentView.addView(bodyView)

                // ✅ Social Context (Facebook only)
                val socialContextView = TextView(ctx).apply {
                    setTextColor(white.toArgb())
                    setPadding(10)
                    textSize = 14f
                    visibility = View.GONE
                }
                contentView.addView(socialContextView)
                val iconView2 = ImageView(ctx)
                iconView2.setPadding(10)
                iconView2.layoutParams = LinearLayout.LayoutParams(200,200)
                val adText2 = TextView(ctx)
                adText2.setTextColor(white.toArgb())
                adText2.setPadding(10)
                adText2.setBackgroundColor(black.toArgb())
                adText2.text = "Ad"
                adText2.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                contentView.addView(adText2)

                horizontalView.addView(iconView2)
                horizontalView.addView(contentView)
                linearLayout.addView(horizontalView)


                val callToActionView = Button(ctx)
                callToActionView.setBackgroundColor(colorPrimary.toArgb())
                callToActionView.setPadding(10)
                callToActionView.setPadding(10)
//                callToActionView.id = 4
                linearLayout.addView(callToActionView)
//
//
//                val priceView = TextView(ctx)
////                priceView.id = 6
//                linearLayout.addView(priceView)
//
//                val starRatingView = RatingBar(ctx, null, android.R.attr.ratingBarStyleSmall)
//                starRatingView.id = 7
//                starRatingView.numStars = 5
//                linearLayout.addView(starRatingView)

//                val storeView = TextView(ctx)
////                storeView.id = 8
//                linearLayout.addView(storeView)
//
//                val advertiserView = TextView(ctx)
////                advertiserView.id = 9
//                linearLayout.addView(advertiserView)
                linearLayout.setBackgroundColor(backGroundColor.toArgb())

                adView.addView(linearLayout)
                adView.headlineView = headlineView
                adView.mediaView = mediaView
                adView.bodyView = bodyView
                adView.iconView = iconView
                adView.callToActionView = callToActionView
                adView.iconView = iconView2
//                adView.priceView = priceView
//                adView.starRatingView = starRatingView
//                adView.storeView = storeView
//                adView.advertiserView = advertiserView

                populateNativeAdView(ad, adView)

                // ✅ Set Facebook social context if available
                val extras = ad.extras
                if (extras.containsKey(FacebookMediationAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {
                    val socialContext =
                        extras.getString(FacebookMediationAdapter.KEY_SOCIAL_CONTEXT_ASSET)
                    socialContextView.text = socialContext
                    socialContextView.visibility = View.VISIBLE
                } else {
                    socialContextView.visibility = View.GONE
                }

                adView
            },
            update = { adView ->
                populateNativeAdView(ad, adView)
            }
        )
    }
}

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    adView.setNativeAd(nativeAd)

    (adView.headlineView as TextView).text = nativeAd.headline
    if (nativeAd.body == null) {
        adView.bodyView?.visibility = android.view.View.INVISIBLE
    } else {
        adView.bodyView?.visibility = android.view.View.VISIBLE
        (adView.bodyView as TextView).text = nativeAd.body
    }
    if (nativeAd.callToAction == null) {
        adView.callToActionView?.visibility = android.view.View.INVISIBLE
    } else {
        adView.callToActionView?.visibility = android.view.View.VISIBLE
        (adView.callToActionView as Button).text = nativeAd.callToAction
    }


    if (adView.mediaView != null) {
        adView.mediaView?.mediaContent = nativeAd.mediaContent
    } else {
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = android.view.View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = android.view.View.VISIBLE
        }
    }
    if (nativeAd.icon == null) {
        adView.iconView?.visibility = android.view.View.GONE
    } else {
        (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView?.visibility = android.view.View.VISIBLE
    }
//
//    if (nativeAd.price == null) {
//        adView.priceView?.visibility = android.view.View.INVISIBLE
//    } else {
//        adView.priceView?.visibility = android.view.View.VISIBLE
//        (adView.priceView as TextView).text = nativeAd.price
//    }
//
//    if (nativeAd.store == null) {
//        adView.storeView?.visibility = android.view.View.INVISIBLE
//    } else {
//        adView.storeView?.visibility = android.view.View.VISIBLE
//        (adView.storeView as TextView).text = nativeAd.store
//    }
//
//    if (nativeAd.starRating == null) {
//        adView.starRatingView?.visibility = android.view.View.INVISIBLE
//    } else {
//        adView.starRatingView?.visibility = android.view.View.VISIBLE
//        (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
//    }
//
//    if (nativeAd.advertiser == null) {
//        adView.advertiserView?.visibility = android.view.View.INVISIBLE
//    } else {
//        adView.advertiserView?.visibility = android.view.View.VISIBLE
//        (adView.advertiserView as TextView).text = nativeAd.advertiser
//    }
}
