package com.xdroid.app.changewallpaper.ui.layouts

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.cmodel.AdModel
import com.xdroid.app.changewallpaper.cmodel.ItemModel
import com.xdroid.app.changewallpaper.cmodel.ListItems
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.ui.adscreen.AdmobNativeAd
import com.xdroid.app.changewallpaper.ui.adscreen.BannerAdView2
import com.xdroid.app.changewallpaper.ui.adscreen.NativeAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.RewardedAdManager
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.components.AutoAdSliderNetwork
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogImage
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.dialogs.LoadingAlertDialog
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.latoRegular12
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.enums.Status
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.DynamicResponse
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.helpers.isNull
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import com.xdroid.app.changewallpaper.utils.vm.SharedViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Random


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
//    dashVM: DashVM = koinInject(),
//    navigateToDetail: (WatchlistMovie) -> Unit
    viewModel: SharedViewModel
) {
    val homeViewModel: HomeViewModel = koinViewModel()

    val states by homeViewModel.imageResponse.collectAsState()
    val isDataLoaded = rememberSaveable { mutableStateOf(false) }

    val adBanner by homeViewModel.adBanner.collectAsState(null)

    LaunchedEffect(Unit) {
        if (!isDataLoaded.value) {
            homeViewModel.getAllImage()
        }
        homeViewModel.getADList()// Set as loaded to prevent future calls

    }
    var itemModel by rememberSaveable { mutableStateOf(ItemModel()) }
    var showView by rememberSaveable { mutableStateOf(false) }
    var showAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }
//    val myImages = remember { mutableStateListOf<MyItems>() }
    val myImages by homeViewModel.myImages.collectAsState(initial = emptyList())

    val dataImages = remember { mutableStateListOf<MyItems>() }
    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
    val context = LocalContext.current
    val activity = context as? Activity
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Wallpaper Set Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Wallpaper Setting Cancelled!", Toast.LENGTH_SHORT).show()
        }
    }

    var finalImage by remember { mutableStateOf("") }
    var showRewards by rememberSaveable { mutableStateOf(false) }

    var showExit by rememberSaveable { mutableStateOf(false) }

//    if (itemModel.items != null) {
//        createItemModel(itemModel, dataImages, myImages)
//    }
    when (states.status) {
        Status.ERROR -> {
            LaunchedEffect(Unit) {
                showView = false
                alertMessage = states.message ?: "Something went wrong"
                showAlert = true
            }
        }

        Status.SUCCESS -> {
            LaunchedEffect(Unit) {
                if (!isDataLoaded.value) {
                    val response = DynamicResponse.myObject<ItemModel>(states.data)
                    itemModel = response
                    isDataLoaded.value = true
                    createItemModel(itemModel, dataImages, homeViewModel)
                }


                showView = true
                showAlert = false
            }
        }

        Status.IDLE -> {
//            DebugMode.e("data Idle state")


        }

        Status.LOADING -> {
            showView = false

//            DebugMode.e("data loading state")


        }
    }

    val networkHelper: NetworkHelper = koinInject()

    BackHandler {
        showExit = true
    }
    if (showExit) {
        CustomAlertDialogWithAds(
            title = "Exit",
            message = "Are you sure you want close the app?",
            dismissOnClickedOutside = false,
            dismissOnBackPress = false,
            onConfirmButtonClick = {
                showExit = false
                closeApp(context)
            }) {
            showExit = false
        }
    }

    isAmazonDevice()
    Surface {
        val appBarMaxHeightPx = with(LocalDensity.current) { 170 }
        val connection = remember(appBarMaxHeightPx) {
            CollapsingAppBarNestedScrollConnection(appBarMaxHeightPx)
        }
        val density = LocalDensity.current
        val spaceHeight by remember(density) {
            derivedStateOf {
                with(density) {
                    (appBarMaxHeightPx + connection.appBarOffset).toDp()
                }
            }
        }

        Scaffold(
            floatingActionButton = {

                FloatingActionButton(onClick = {
                    if (networkHelper.isNetworkConnected()) {

                        activity?.let {
                            RewardedAdManager.showAd(it) { _ ->
                                val ite = myImages[Random().nextInt(myImages.size)]
                                val image = "${ite.collectionID}/${ite.id}/${ite.image}"
                                finalImage = imageUrl + image
                                DebugMode.e("Items in Settings $finalImage")
                                showRewards = true
                            }
                        }
//            }
                    }
                }, containerColor = white, modifier = Modifier.clip(CircleShape)) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        tint = black,
                        contentDescription = "Reward Button"
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(black)
                    .padding(bottom = 2.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {


                Box(Modifier.nestedScroll(connection)) {
                    Column(
                        modifier = Modifier
                    ) {
                        Spacer(
                            Modifier
                                .padding(4.dp)
                                .height(spaceHeight)
                        )

                        if (!showView)
                            LoadingContent()
                        //                    CircularProgressIndicator(color = Color.White)

                        if (showView) {
                            //                DebugMode.e("Show view $showView")
                            if (myImages.size > 0)
                                ActionsItemList(
                                    items = myImages,
                                    navController = navController,
                                    adBanner = adBanner
                                )
                            else {
                                Text("No Data", color = white, style = latoRegular12)
                            }

                        }
                    }
                    Column(modifier = Modifier.offset { IntOffset(0, connection.appBarOffset) }) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(white),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    context.getString(R.string.app_name),
                                    fontSize = 18.sp,
                                    color = black
                                )
                                IconButton(onClick = {
                                    navController.navigate(ScreenName.Settings)
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Default.Help,
                                        contentDescription = "Help",
                                        tint = black
                                    )
                                }
                            }
                        }

                    }
                }





                if (showAlert) {
                    InfoAlertDialog(message = alertMessage) {
                        showAlert = false
                    }
                }
            }

        }

        if (showRewards) {
            CustomAlertDialogImage(
                image = finalImage,
                confirmButtonText = "Change",
                dismissButtonText = "Cancel",
                onConfirmButtonClick = {
                    showRewards = false
                    changeWallpaper2(
                        context,
                        imageUrl = finalImage,
                        callback = { success ->
                            if (success)
                                Toast.makeText(context, "Wallpaper Changed", Toast.LENGTH_SHORT)
                                    .show()
                        },
                        wallpaperManager = wallpaperManager,
                        launcher = wallpaperLauncher
                    )
                }
            ) {
                showRewards = false
            }
        }

    }
}

fun createItemModel(
    itemModel: ItemModel,
    dataImages: SnapshotStateList<MyItems>,
//    myImages: SnapshotStateList<MyItems>
    viewModel: HomeViewModel
) {

    val items = ArrayList<MyItems>()
    if (itemModel.items?.size!! > 0)
        for (data in itemModel.items!!) {
            val id = data.id
            val colID = data.collectionID
            val createdAt = data.created
            dataImages.clear()
            for (img in data.images!!) {
                dataImages.add(
                    MyItems(
                        collectionID = colID,
                        id = id,
                        image = img,
                        created = createdAt
                    )
                )
            }
            items.addAll(dataImages)


        }

    viewModel.setList(items)
}

@Composable
fun ActionsItemList(
    items: List<MyItems>?,
    navController: NavController,
    adBanner: AdModel? = null
) {
    // Get the screen width
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Assuming a fixed item width, adjust the number of columns based on screen width
    val itemWidth = 120.dp // Set the desired width for each item
    val count = (screenWidth / itemWidth).toInt()

    val newitems = remember(items) {
        itemsWithAds(items)
    }

    val context = LocalContext.current
    val adUnitIds = remember { context.getString(R.string.centerBanner) }

    // Keep track of the loading state
    var isLoading by remember { mutableStateOf(true) }
    var isAdError by remember { mutableStateOf(false) }

    // Use a static AdView instance to avoid recreation
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.LARGE_BANNER)
            adUnitId = adUnitIds
        }
    }
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    // Load the ad when the screen appears
    LaunchedEffect(Unit) {
        NativeAdManager.loadNativeAd(context) { ad ->
            nativeAd = ad
        }
    }

    // Set the AdListener only once
    DisposableEffect(adView) {
        val adListener = object : AdListener() {
            override fun onAdLoaded() {
                DebugMode.e("Banner ad load success")
                isLoading = false
                isAdError = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                DebugMode.e("Banner ad load Failed-> ${error.message}")
                isLoading = false
                isAdError = true
            }
        }
        adView.adListener = adListener

        onDispose {
            adView.adListener = object : AdListener() {}
        }
    }

    // Load the ad only once
    LaunchedEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
    }

//    val count = 2
//    LazyVerticalStaggeredGrid
    LazyVerticalGrid(
        columns = GridCells.Fixed(count),
//        verticalItemSpacing = 2.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

//        if (items != null)
//            items(items.size) { index ->
//                val images = "${items[index].collectionID}/${items[index].id}/${items[index].image}"
//                val rememberImages = remember {
//                    images
//                }
//                ActionItems(rememberImages, navController)
//            }


//        itemsIndexed(items!!) { index, data ->
//            // After every 9 items (or every 3 rows), add a full-width ad
//            DebugMode.e("data index $index")
//            DebugMode.e("data index ${index % 5 == 0 && index != 0}")
//            if (index % 6 == 0 && index != 0) {
//                item(span = { GridItemSpan(3) }) { // Span full width of 3 columns
//                    AdComposable()
//                }
//            }
//
//                val images =
//                    "${data.collectionID}/${data.id}/${data.image}"
//                val rememberImages = remember {
//                    images
//                }
//                ActionItems(rememberImages, navController)
//
//        }
//        DebugMode.e("regenerate  ${newitems.size} increase?")


        items(newitems.size, key = { index ->
            (if (newitems[index] is MyItems) {
                val ite = newitems[index] as MyItems
                val images =
                    "${ite.collectionID}/${index}/${ite.id}/${ite.created}/${index * 0.1}/${ite.image}"
                images// or a unique identifier for MyItems
            } else {
                "ad_$index"  // Assign a unique key for ad items
            })!!

        }, span = { index ->
            (if (newitems[index] is MyItems) {
                GridItemSpan(1) // or a unique identifier for MyItems
            } else {
                GridItemSpan(3)  // Assign a unique key for ad items
            })!!

        }) { index ->
            if (newitems[index] is MyItems) {
                val ite = newitems[index] as MyItems
                val images =
                    "${ite.collectionID}/${ite.id}/${ite.image}"
                val rememberImages = remember {
                    images
                }
                ActionItems(rememberImages, navController)
            } else {
                AdComposable(adView, isLoading, isAdError, adBanner, nativeAd)

            }

        }


    }
}

fun itemsWithAds(items: List<MyItems>?): List<Any> {
    val mixedList = mutableListOf<Any>()
    items?.forEachIndexed { index, item ->
        mixedList.add(item)
        // Add an ad placeholder every `interval` items
//        DebugMode.e("calculated Value ${(index + 1) % 15}")
        if ((index + 1) % 15 == 0) {
            mixedList.add("AdItem")
        }

    }
    return mixedList
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ActionItems(
    item: String?,
    navController: NavController,
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    var counter by remember {
        mutableIntStateOf(App.preferenceHelper.getValue(PrefConstant.COUNTER, 0) as Int)
    }
    val imageUrl = remember { UrlName.imageUrl + item }
    var navigate by remember {
        mutableStateOf(
            false
        )
    }
    var showloading by remember {
        mutableStateOf(
            false
        )
    }

    var isLoading by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {

                navigate = true

            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Spacer(modifier = Modifier.height(10.dp))
//        if (isLoading) {
//            // Show the circular loading indicator while loading
//            SingleShimmer()
//        }
        GlideImage(
            model = imageUrl,
            contentDescription = item,
            loading = placeholder(R.drawable.rectange),
            transition = CrossFade,
            modifier = Modifier
                .height(250.dp)
                .width(450.dp)
//                .fillMaxWidth()
//                    .graphicsLayer { alpha = if (isLoading) 0f else 1f }
                .clip(
                    RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.Crop

        )


        Spacer(modifier = Modifier.height(5.dp))


    }

    if (navigate) {
        val screen = ScreenName.Detail
        navigate = false
        if (counter < 5) {
            navController.navigate(
                ScreenName.detailRoute(
                    screen,
//                        UrlName.imageUrl + "${item?.collectionID}/${item?.id}/${item?.image}"
                    Uri.parse(UrlName.imageUrl + item).toString()
                )
            )
            counter += 1
            App.preferenceHelper.setValue(PrefConstant.COUNTER, counter)
            counter = App.preferenceHelper.getValue(PrefConstant.COUNTER, 0) as Int
        } else {
            showloading = true
            showInterstitial(LocalContext.current) {

                navController.navigate(
                    ScreenName.detailRoute(
                        screen,
//                        UrlName.imageUrl + "${item?.collectionID}/${item?.id}/${item?.image}"
                        UrlName.imageUrl + item
                    )
                )
                counter = 0
                showloading = false
                App.preferenceHelper.setValue(PrefConstant.COUNTER, counter)
                counter = App.preferenceHelper.getValue(PrefConstant.COUNTER, 0) as Int
            }
        }


    }

    if (showloading) {
        LoadingAlertDialog()
    }

}


@Composable
fun AdComposable(
    adView: AdView,
    isLoading: Boolean,
    isAdError: Boolean,
    adBanner: AdModel?,
    nativeAd: NativeAd?
) {
    var nativeAd2 by remember { mutableStateOf<NativeAd?>(null) }


    val context = LocalContext.current
    // Load the ad when the screen appears
    if (nativeAd == null) {
        LaunchedEffect(Unit) {
            NativeAdManager.loadNativeAd(context) { ad ->
                nativeAd2 = ad
            }
        }
    } else {
        LaunchedEffect(Unit) {
            nativeAd2 = nativeAd
        }
    }
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(5.dp))

        BannerAdView2()
        Spacer(modifier = Modifier.height(5.dp))
        AdSection(adBanner)
        Spacer(modifier = Modifier.height(5.dp))
        if (nativeAd2 == null) {
            ShimmerAdPlaceHolder()
        } else {
            AdmobNativeAd(nativeAd2)
        }
//        AdmobNativeAd(nativeAd2)
//        ListBannerAdView(adView, isLoading, isAdError)

        Spacer(modifier = Modifier.height(5.dp))

    }

}


fun closeApp(context: Context) {
    // Use the appropriate method to close the app
    // Example for an Activity:
    (context as Activity).finish()
}


@Composable
fun AdSection(adBanner: AdModel? = null) {
    if (adBanner != null) {
        if (adBanner.items?.isNotEmpty().isNull()) {
            val ban = adBanner.items!!
            if (ban.isNotEmpty()) {
                AutoAdSliderNetwork(banner = ban)
            }
        }
    }


}

class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Int
) : NestedScrollConnection {

    var appBarOffset: Int by mutableIntStateOf(0)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y.toInt()
        val newOffset = appBarOffset + delta
        val previousOffset = appBarOffset
        appBarOffset = newOffset.coerceIn(-appBarMaxHeight, 0)
        val consumed = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }
}

fun isAmazonDevice(): Boolean {
    val manufacturer = Build.MANUFACTURER
    DebugMode.e(manufacturer)
    return manufacturer.equals("Amazon", ignoreCase = true)
}