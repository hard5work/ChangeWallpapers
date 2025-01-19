package com.xdroid.app.changewallpaper.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.cmodel.AdModel
import com.xdroid.app.changewallpaper.cmodel.DefaultRequestModel
import com.xdroid.app.changewallpaper.cmodel.ErrorModel
import com.xdroid.app.changewallpaper.data.UrlName
import com.xdroid.app.changewallpaper.data.repository.MainRepository
import com.xdroid.app.changewallpaper.utils.constants.NetworkError
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.DynamicResponse
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.helpers.PreferenceHelper
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
//    private lateinit var mNavigator: WeakReference<N>

    val preferenceHelper = PreferenceHelper(App.baseApplication)

    var error = MutableStateFlow(ErrorModel("", ""))

    val getError: MutableStateFlow<ErrorModel>
        get() = error


    private var adBannerRequest = MutableStateFlow<Resource<JsonObject>>(Resource.idle())

    val adBannerResponse: StateFlow<Resource<JsonObject>>
        get() = adBannerRequest


    private var _adBanner = MutableStateFlow<AdModel?>(null)
    val adBanner: StateFlow<AdModel?>
        get() = getAdModel()


    fun getCompositeDisposable() = compositeDisposable


    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage


    // Method to simulate an error
    fun simulateError(error: String) {
        _errorMessage.value = error
    }

    // Method to clear the error message
    fun clearError() {
        _errorMessage.value = null
    }


    fun getADList(
    ) {
        val requestModel = DefaultRequestModel()
        requestModel.url = UrlName.adUrl
        requestModel.setToken = false
        requestGetMethodDispose(requestModel, adBannerRequest)
    }

    private fun getAdModel(): MutableStateFlow<AdModel?> {
        _adBanner.value = DynamicResponse.myObject<AdModel>(adBannerResponse.value.data)
        return _adBanner
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        compositeDisposable.dispose()
        DebugMode.e("BaseViewModel", "function onCleared() is called.")
    }

    fun requestPostMethod(
        requestModel: DefaultRequestModel,
        liveData: MutableStateFlow<Resource<JsonObject>>
    ) {
        viewModelScope.launch {
            liveData.value = Resource.loading()
            if (networkHelper.isNetworkConnected())
                compositeDisposable.add(mainRepository.postMethodComposite(requestModel, liveData))
            else {
                liveData.value = Resource.error(NetworkError.NO_INTERNET_CONNECTION)
            }
        }
    }


    fun requestGetMethodDispose(
        requestModel: DefaultRequestModel,
        liveData: MutableStateFlow<Resource<JsonObject>>
    ) {
        viewModelScope.launch {
            liveData.value = Resource.loading()
            if (networkHelper.isNetworkConnected())
                compositeDisposable.add(mainRepository.getMethodComposite(requestModel, liveData))
            else {
                liveData.value = Resource.error(NetworkError.NO_INTERNET_CONNECTION)
            }
        }
    }


    fun requestPutMethod(
        requestModel: DefaultRequestModel,
        liveData: MutableStateFlow<Resource<JsonObject>>
    ) {
        viewModelScope.launch {
            liveData.value = Resource.loading()
            if (networkHelper.isNetworkConnected())
                compositeDisposable.add(
                    mainRepository.putMethodComposite(
                        requestModel,
                        liveData
                    )
                )
            else {
                liveData.value = Resource.error(NetworkError.NO_INTERNET_CONNECTION)
            }
        }
    }


    fun requestPatchMethod(
        requestModel: DefaultRequestModel,
        liveData: MutableStateFlow<Resource<JsonObject>>
    ) {
        viewModelScope.launch {
            liveData.value = Resource.loading()
            if (networkHelper.isNetworkConnected())
                compositeDisposable.add(
                    mainRepository.patchMethodComposite(
                        requestModel,
                        liveData
                    )
                )
            else {
                liveData.value = Resource.error(NetworkError.NO_INTERNET_CONNECTION)
            }
        }
    }


    fun requestDeleteMethod(
        requestModel: DefaultRequestModel,
        liveData: MutableStateFlow<Resource<JsonObject>>
    ) {
        viewModelScope.launch {
            liveData.value = Resource.loading()
            if (networkHelper.isNetworkConnected())
                compositeDisposable.add(
                    mainRepository.deleteMethodComposite(
                        requestModel,
                        liveData
                    )
                )
            else {
                liveData.value = Resource.error(NetworkError.NO_INTERNET_CONNECTION)
            }
        }
    }

}