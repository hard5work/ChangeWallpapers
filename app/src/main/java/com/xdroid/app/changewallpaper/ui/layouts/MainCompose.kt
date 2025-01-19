package com.xdroid.app.changewallpaper.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.utils.helpers.PreferenceHelper


@Composable
fun MyApp() {
    val navController = rememberNavController()
    /*All navigation are included here and all navigation are done from here.*/
    /*Add new route to app for navigation*/

//    var lastVisitedScreen by rememberSaveable { mutableStateOf(ScreenName.Home) }

    val currentScreen by rememberSaveable { mutableStateOf(ScreenName.Home) }
    NavHost(navController, startDestination = currentScreen) {
        composable(ScreenName.Home) {
            HomeScreen(navController)
        }
        composable(ScreenName.Detail + "?url={url}") { backstack ->
            val movieUrl = backstack.arguments?.getString("url") ?: ""
            WallpaperChangerApp(navController,movieUrl)
        }

    }

//    LaunchedEffect(navController) {
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            lastVisitedScreen = destination.route ?:ScreenName.Home
//        }
//    }
}