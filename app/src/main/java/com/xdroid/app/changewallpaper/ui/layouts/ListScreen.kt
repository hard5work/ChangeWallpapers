package com.xdroid.app.changewallpaper.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.cmodel.Item
import com.xdroid.app.changewallpaper.cmodel.ItemModel
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.background
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.enums.Status
import com.xdroid.app.changewallpaper.utils.helpers.DynamicResponse
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun HomeScreen(
    navController: NavController
//    dashVM: DashVM = koinInject(),
//    navigateToDetail: (WatchlistMovie) -> Unit
) {
    val homeViewModel: HomeViewModel = koinViewModel()

    val states by homeViewModel.imageResponse.collectAsState(initial = Resource.idle())

    LaunchedEffect(Unit) {
        homeViewModel.getAllImage()
    }
    val itemModel = remember { mutableStateOf(ItemModel()) }
    var showView by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var myImages by remember { mutableStateOf(ArrayList<MyItems>()) }


    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

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
                        val response = DynamicResponse.myObject<ItemModel>(states.data)
                        itemModel.value = response
                        if (itemModel.value.items?.size!! > 0)
                            for (data in itemModel.value.items!!) {
                                val id = data.id
                                val colID = data.collectionID
                                for (img in data.images!!) {
                                    myImages.add(
                                        MyItems(
                                            collectionID = colID,
                                            id = id,
                                            image = img
                                        )
                                    )
                                }
                            }

                        showView = true
                        showAlert = false
                    }
                }
                Status.IDLE -> {
                    showView = false


                }
                Status.LOADING -> {
                    showView = false
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(background)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround,
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }

                }
            }
            if (showView)
                if (itemModel.value.items?.size!! > 0)
                    ActionsItemList(
                        items = myImages,
                        navController = navController
                    )

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
    val count = 2
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        if (items != null)
            items(items.size) { index ->
                val images = "${items[index].collectionID}/${items[index].id}/${items[index].image}"
                ActionItems(images, navController)
            }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ActionItems(
    item: String?,
    navController: NavController,
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    Column(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                val screen = ScreenName.Detail
                navController.navigate(
                    ScreenName.detailRoute(
                        screen,
//                        UrlName.imageUrl + "${item?.collectionID}/${item?.id}/${item?.image}"
                        UrlName.imageUrl + item
                    )
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        GlideImage(
            model = UrlName.imageUrl + item,
            contentDescription = item
        )
        Spacer(modifier = Modifier.height(5.dp))


    }
}
