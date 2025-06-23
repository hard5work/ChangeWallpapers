package com.xdroid.app.changewallpaper.utils.vm

import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.crashlytics.internal.common.BuildIdInfo
import com.google.gson.JsonObject
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.cmodel.DefaultRequestModel
import com.xdroid.app.changewallpaper.cmodel.MyItems
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.repository.MainRepository
import com.xdroid.app.changewallpaper.ui.base.BaseViewModel
import com.xdroid.app.changewallpaper.ui.screens.ScreenName
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.helpers.isNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random


class HomeViewModel(mainRepository: MainRepository, networkHelper: NetworkHelper) :
    BaseViewModel(mainRepository, networkHelper) {

    private var imageRequest = MutableStateFlow<Resource<JsonObject>>(Resource.idle())

    val imageResponse: StateFlow<Resource<JsonObject>>
        get() = imageRequest

    private val _isDataLoaded = MutableStateFlow(false)
    val isInit: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    val _myImages = MutableStateFlow<List<MyItems>>(emptyList())
    val myImages: StateFlow<List<MyItems>>
        get() = _myImages

    fun getAllImage() {
        val request = DefaultRequestModel()
        if (BuildConfig.FLAVOR == "all")
            request.url = UrlName.allImages
        else if (BuildConfig.FLAVOR == "anim" || BuildConfig.FLAVOR == "anime")
            request.url = UrlName.animeImages
        else
            request.url = UrlName.allImageList

        requestGetMethodDispose(request, imageRequest)

    }

    fun resetDATA() {
        imageRequest = MutableStateFlow<Resource<JsonObject>>(Resource.idle())
    }

    fun setList(list: List<MyItems>) {
        _myImages.value = list.shuffled(Random())
    }

    private var _feedbackRequest = MutableStateFlow<Resource<JsonObject>>(Resource.idle())

    val feedbackResponse: StateFlow<Resource<JsonObject>>
        get() = _feedbackRequest

    private val _title = mutableStateOf<String?>("")
    val title: State<String?> = _title

    private val _content = mutableStateOf<String?>("")
    val content: State<String?> = _content


    private fun postFeedback(data: JsonObject) {
        val request = DefaultRequestModel()
        request.url = UrlName.feedback
        request.setToken = true
        request.body = data
        requestPostMethod(request, _feedbackRequest)
    }


    fun verifyPosts(
        title: String?,
        content: String?
    ) {
        clearLoginError()
        if (title.isNull().isEmpty()) {
            _title.value = "Title is required."
        } else if (content.isNull().isEmpty()) {
            _content.value = "Content is required."
        } else {
            clearLoginError()
            val obj = JsonObject()
            obj.addProperty("title", title)
            obj.addProperty("message", content)
            obj.addProperty("app_id", BuildConfig.APP_ID)
            obj.addProperty("device_type",  Build.MANUFACTURER)
            obj.addProperty("versions", "${BuildConfig.VERSION_NAME} [${BuildConfig.VERSION_CODE}]")
            obj.addProperty("deviceName", "${Build.MODEL}_${Build.DEVICE}")
            postFeedback(obj)
        }


    }

    // Method to clear the error message
    fun clearLoginError() {
        _title.value = ""
        _content.value = ""
    }

}