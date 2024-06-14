package com.xdroid.app.changewallpaper.ui.layouts

import androidx.compose.runtime.Composable
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
    App.preferenceHelper = PreferenceHelper(LocalContext.current)
    /*All navigation are included here and all navigation are done from here.*/
    /*Add new route to app for navigation*/

    NavHost(navController, startDestination = ScreenName.Home) {
        composable(ScreenName.Home) {
            HomeScreen(navController)
        }
        composable(ScreenName.Detail + "?url={url}") { backstack ->
            val movieUrl = backstack.arguments?.getString("url") ?: ""
            WallpaperChangerApp(movieUrl)
        }

    }
}