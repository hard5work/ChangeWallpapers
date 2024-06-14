package com.xdroid.app.changewallpaper.utils.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.google.gson.JsonObject
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.cmodel.DefaultRequestModel
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.repository.MainRepository
import com.xdroid.app.changewallpaper.ui.base.BaseViewModel
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class HomeViewModel(mainRepository: MainRepository, networkHelper: NetworkHelper) :
    BaseViewModel(mainRepository, networkHelper) {

    private var imageRequest = MutableStateFlow<Resource<JsonObject>>(Resource.idle())

    val imageResponse: StateFlow<Resource<JsonObject>>
        get() = imageRequest

    var isInit = false

    fun getAllImage() {
        if (!isInit) {
            isInit = true
            val request = DefaultRequestModel()
            if (BuildConfig.FLAVOR == "all")
                request.url = UrlName.allImages
            else
                request.url = UrlName.allImageList

            requestGetMethodDispose(request, imageRequest)
        }
    }


}