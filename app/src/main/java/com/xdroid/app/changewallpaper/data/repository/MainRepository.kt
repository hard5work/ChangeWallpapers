package com.xdroid.app.changewallpaper.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.cmodel.DefaultRequestModel
import com.xdroid.app.changewallpaper.cmodel.ErrorModel
import com.xdroid.app.changewallpaper.data.neworks.ApiHelper
import com.xdroid.app.changewallpaper.di.module.getErrorMessage
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.PreferenceHelper
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow

class MainRepository(private val apiHelper: ApiHelper) {

    private val preferenceHelper = PreferenceHelper(App.baseApplication)

    private fun jsonParsers(data: String): ErrorModel {
        return try {
            val jsonParser = JsonParser()
            val jsonObject = jsonParser.parse(data).asJsonObject
            Gson().fromJson(
                jsonObject,
                object : TypeToken<ErrorModel>() {}.type
            )
        } catch (e: Exception) {
            ErrorModel("API Error", e.message.toString())
        }

    }


    suspend fun getMethodComposite(
        defaultRequestModel: DefaultRequestModel,
        values: MutableStateFlow<Resource<JsonObject>>
    ): Disposable {
        defaultRequestModel.headers = HashMap()
        if (defaultRequestModel.setToken) {
            defaultRequestModel.headers["token"] =
                preferenceHelper.getValue(PrefConstant.AUTH_TOKEN, "") as String
        }
        DebugMode.e("getMethodComposite", "header" + defaultRequestModel.headers)
        values.value = Resource.loading()
        return apiHelper.getMethod(defaultRequestModel.url, defaultRequestModel.headers)
            .subscribeOn(Schedulers.io())
            .subscribe({
                DebugMode.e("getMethodComposite", "onSuccess  $it")
                values.value = Resource.success(it)
            }, {
//                DebugMode.e("MainRepository", "onFailed $it ")
//                values.postValue(Resource.error(getErrorMessage(it)!!, null))
                val model = jsonParsers(getErrorMessage(it)!!)
                errorModel(values, model)
            })
    }


    suspend fun postMethodComposite(
        defaultRequestModel: DefaultRequestModel,
        values: MutableStateFlow<Resource<JsonObject>>
    ): Disposable {
        defaultRequestModel.headers = HashMap()
        if (defaultRequestModel.setToken) {
            defaultRequestModel.headers["Authorization"] =
                "Bearer ${preferenceHelper.getValue(PrefConstant.AUTH_TOKEN, "") as String}"
        }

        DebugMode.e("postMethodComposite", "body" + defaultRequestModel.body)
        DebugMode.e("postMethodComposite", "url" + defaultRequestModel.url)
        DebugMode.e("postMethodComposite", "header" + defaultRequestModel.headers)
        values.value = Resource.loading()
        return apiHelper.postMethod(
            defaultRequestModel.url,
            defaultRequestModel.headers,
            defaultRequestModel.body
        )
            .subscribeOn(Schedulers.io())
            .subscribe({
                DebugMode.e("postMethodComposite", "onSuccess  $it")
                values.value = Resource.success(it)
            }, {
//                DebugMode.e("MainRepository", "onFailed $it")
                val model = jsonParsers(getErrorMessage(it)!!)
                errorModel(values, model)
//                values.postValue(Resource.error(getErrorMessage(it)!!, null))
            })
    }

    private fun errorModel(
        values: MutableLiveData<Resource<JsonObject>>,
        model: ErrorModel
    ) {
        values.postValue(Resource.error(model.message, model.title))
    }

    private fun errorModel(
        values: MutableStateFlow<Resource<JsonObject>>,
        model: ErrorModel
    ) {
        values.value = Resource.error(model.message, model.title)
    }

    private fun errorModelArray(
        values: MutableStateFlow<Resource<JsonArray>>,
        model: ErrorModel
    ) {
        values.value = Resource.error(model.message, model.title)
    }


    suspend fun putMethodComposite(
        defaultRequestModel: DefaultRequestModel,
        values: MutableStateFlow<Resource<JsonObject>>
    ): Disposable {
        defaultRequestModel.headers = HashMap()
        if (defaultRequestModel.setToken) {
            defaultRequestModel.headers["Authorization"] =
                "Bearer ${preferenceHelper.getValue(PrefConstant.AUTH_TOKEN, "") as String}"
        }
        values.value = Resource.loading()
        return apiHelper.putMethod(
            defaultRequestModel.url,
            defaultRequestModel.headers,
            defaultRequestModel.loginBody
        )
            .subscribeOn(Schedulers.io())
            .subscribe({
                DebugMode.e("putMethod", "onSuccess  $it")
                values.value = Resource.success(it)
            }, {
//                DebugMode.e("MainRepository", "onFailed $it")

                val model = jsonParsers(getErrorMessage(it)!!)

                DebugMode.e("putMethod", "onFailed  ${model.message}")
                errorModel(values, model)
//                values.postValue(Resource.error(getErrorMessage(it)!!, null))
            })
    }


    suspend fun patchMethodComposite(
        defaultRequestModel: DefaultRequestModel,
        values: MutableStateFlow<Resource<JsonObject>>
    ): Disposable {
        defaultRequestModel.headers = HashMap()
        if (defaultRequestModel.setToken) {
            defaultRequestModel.headers["Authorization"] =
                "Bearer ${preferenceHelper.getValue(PrefConstant.AUTH_TOKEN, "") as String}"
        }
        values.value = Resource.loading()
        return apiHelper.patchMethod(
            defaultRequestModel.url,
            defaultRequestModel.headers,
            defaultRequestModel.loginBody
        )
            .subscribeOn(Schedulers.io())
            .subscribe({
                DebugMode.e("putMethod", "onSuccess  $it")
                values.value = Resource.success(it)
            }, {
//                DebugMode.e("MainRepository", "onFailed $it")

                val model = jsonParsers(getErrorMessage(it)!!)

                DebugMode.e("putMethod", "onFailed  ${model.message}")
                errorModel(values, model)
//                values.postValue(Resource.error(getErrorMessage(it)!!, null))
            })
    }

    suspend fun deleteMethodComposite(
        defaultRequestModel: DefaultRequestModel,
        values: MutableStateFlow<Resource<JsonObject>>
    ): Disposable {
        defaultRequestModel.headers = HashMap()
        if (defaultRequestModel.setToken) {
            defaultRequestModel.headers["Authorization"] =
                "Bearer ${preferenceHelper.getValue(PrefConstant.AUTH_TOKEN, "") as String}"
        }
        values.value = Resource.loading()
        return apiHelper.deleteMethod(
            defaultRequestModel.url,
            defaultRequestModel.headers
        )
            .subscribeOn(Schedulers.io())
            .subscribe({
                DebugMode.e("deleteMethod", "onSuccess  $it")
                values.value = Resource.success(it)
            }, {
//                DebugMode.e("MainRepository", "onFailed $it")

                val model = jsonParsers(getErrorMessage(it)!!)

                DebugMode.e("deleteMethod", "onFailed  ${model.message}")
                errorModel(values, model)
//                values.postValue(Resource.error(getErrorMessage(it)!!, null))
            })
    }
}