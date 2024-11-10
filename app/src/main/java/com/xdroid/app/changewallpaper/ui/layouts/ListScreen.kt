package com.xdroid.app.changewallpaper.ui.layouts

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.cmodel.ItemModel
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.ui.adscreen.ListBannerAdView
import com.xdroid.app.changewallpaper.ui.adscreen.showInterstitial
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.dialogs.LoadingAlertDialog
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.ui.theme.shimmerColor3
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.enums.Status
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.DynamicResponse
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Random


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController
//    dashVM: DashVM = koinInject(),
//    navigateToDetail: (WatchlistMovie) -> Unit
) {
    val homeViewModel: HomeViewModel = koinViewModel()

    val states by homeViewModel.imageResponse.collectAsState()
    val isDataLoaded = rememberSaveable { mutableStateOf(false) }

    if (!isDataLoaded.value) {
        DebugMode.e("askdjhaksjdhjasdahsd ${isDataLoaded.value}")
        LaunchedEffect(Unit) {
            homeViewModel.getAllImage() // Set as loaded to prevent future calls
        }
    }
    val itemModel = rememberSaveable { mutableStateOf(ItemModel()) }
    var showView by rememberSaveable { mutableStateOf(false) }
    var showAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }
    val myImages by rememberSaveable { mutableStateOf(ArrayList<MyItems>()) }
    val dataImages by remember { mutableStateOf(ArrayList<MyItems>()) }


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
                    DebugMode.e("data loaded $response")
                    itemModel.value = response
                    isDataLoaded.value = true
                    if (itemModel.value.items?.size!! > 0)
                        for (data in itemModel.value.items!!) {
                            val id = data.id
                            val colID = data.collectionID
                            for (img in data.images!!) {
                                dataImages.add(
                                    MyItems(
                                        collectionID = colID,
                                        id = id,
                                        image = img
                                    )
                                )
                            }
                            myImages.addAll(dataImages.shuffled(Random()))


                        }

                    showView = true
                    showAlert = false
                }
            }
        }

        Status.IDLE -> {
            showView = false
            DebugMode.e("data Idle state")


        }

        Status.LOADING -> {
            showView = false

            DebugMode.e("data loading state")


        }
    }

    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            if (!showView)
                LoadingContent()
//                    CircularProgressIndicator(color = Color.White)

            if (showView) {
                DebugMode.e("Show view $showView")
                if (itemModel.value.items?.size!! > 0)
                    ActionsItemList(
                        items = myImages,
                        navController = navController
                    )
            }

            if (showAlert) {
                InfoAlertDialog(message = alertMessage) {
                    showAlert = false
                }
            }
        }

    }
}

@Composable
fun ActionsItemList(
    items: List<MyItems>?,
    navController: NavController
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


        DebugMode.e("regenerate  ${newitems.size} increase?")
        items(newitems.size, key = { index ->
            (if (newitems[index] is MyItems) {
                val ite = newitems[index] as MyItems
                val images =
                    "${ite.collectionID}/${ite.id}/${ite.image}"
                images// or a unique identifier for MyItems
            } else {
                "ad_$index"  // Assign a unique key for ad items
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
                AdComposable()
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
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navigate = true

            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
//        if (isLoading) {
//            // Show the circular loading indicator while loading
//            SingleShimmer()
//        }
        GlideImage(
            model = imageUrl,
            contentDescription = item,
            loading = placeholder(R.drawable.shimmer_shape),
//            transition = CrossFade,
            modifier = Modifier
                .height(250.dp)
                .width(300.dp)
//                    .graphicsLayer { alpha = if (isLoading) 0f else 1f }
                .clip(
                    RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.FillHeight

            )


        Spacer(modifier = Modifier.height(5.dp))


    }

    if (navigate) {
        val screen = ScreenName.Detail
        if (counter < 5) {
            navController.navigate(
                ScreenName.detailRoute(
                    screen,
//                        UrlName.imageUrl + "${item?.collectionID}/${item?.id}/${item?.image}"
                    UrlName.imageUrl + item
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
        navigate = false


    }

    if (showloading) {
        LoadingAlertDialog()
    }

}


@Composable
fun AdComposable() {
    Column( modifier = Modifier
        .padding(5.dp)
        .clip(RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(10.dp))

    ListBannerAdView()
        Spacer(modifier = Modifier.height(5.dp))

    }

}

