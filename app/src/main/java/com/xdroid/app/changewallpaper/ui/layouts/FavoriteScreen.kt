package com.xdroid.app.changewallpaper.ui.layouts

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.cmodel.ItemModel
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName.imageUrl
import com.xdroid.app.changewallpaper.data.room.Favorites
import com.xdroid.app.changewallpaper.ui.adscreen.RewardedAdManager
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogImage
import com.xdroid.app.changewallpaper.ui.dialogs.CustomAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.latoRegular12
import com.xdroid.app.changewallpaper.ui.theme.latoRegular16
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.enums.Status
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.DynamicResponse
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.vm.FavoriteViewModel
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import com.xdroid.app.changewallpaper.utils.vm.SharedViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Random


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteScreen(
    navController: NavController
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val favVM: FavoriteViewModel = koinViewModel()

    val states by favVM.notes.collectAsState()
    val isDataLoaded = rememberSaveable { mutableStateOf(false) }

    val adBanner by homeViewModel.adBanner.collectAsState(null)

    LaunchedEffect(Unit) {
        homeViewModel.getADList()// Set as loaded to prevent future calls
    }
    var itemModel by rememberSaveable { mutableStateOf(ItemModel()) }
    var showView by rememberSaveable { mutableStateOf(true) }
    var showAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }
    var originalSize by rememberSaveable { mutableIntStateOf(0) }
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


    val networkHelper: NetworkHelper = koinInject()

    BackHandler {
        navController.navigateUp()
    }

    LaunchedEffect(states.size) {
        DebugMode.e(states.size.toString())
        if (originalSize != states.size)
            createItemModel2(states, dataImages, homeViewModel)
        originalSize = states.size
    }
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(white),
                                        elevation = CardDefaults.cardElevation(5.dp)
                                    ) {
                                        Text(
                                            "No Favorites Wallpapers",
                                            color = black,
                                            style = latoRegular16,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(
                                                vertical = 10.dp,
                                                horizontal = 16.dp
                                            )
                                        )
                                    }
                                }

                            }

                        }
                    }
                    Column(modifier = Modifier.offset { IntOffset(0, connection.appBarOffset) }) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "BackIcon"
                                    )
                                }
                                Text(
                                    text = "Favorites",
                                    fontSize = 18.sp,
                                    color = white,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                    }
                }

            }

        }

    }
}


fun createItemModel2(
    itemModel: List<Favorites>,
    dataImages: SnapshotStateList<MyItems>,
//    myImages: SnapshotStateList<MyItems>
    viewModel: HomeViewModel
) {

    val items = ArrayList<MyItems>()
    if (itemModel.isNotEmpty()) {
        dataImages.clear()
        for (data in itemModel!!) {
            val id = data.id
            val colID = ""
            val createdAt = ""
            dataImages.add(
                MyItems(
                    collectionID = colID,
                    id = "",
                    image = data.url,
                    created = createdAt
                )
            )


        }
        items.addAll(dataImages)
    }

    viewModel.setList(items)
}
