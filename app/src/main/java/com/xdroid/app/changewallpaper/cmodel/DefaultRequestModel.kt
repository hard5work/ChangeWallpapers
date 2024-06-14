package com.xdroid.app.changewallpaper.cmodel

import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class DefaultRequestModel {
    var url: String = ""
    var headers: HashMap<String, String> = HashMap()
    var loginBody: HashMap<String, String> = HashMap()
    var body: JsonObject = JsonObject()
    var setToken: Boolean = false
    var fileBody = HashMap<String, RequestBody>()
    var fileBodyDocs = HashMap<String, List<HashMap<String, RequestBody>>>()
    var fileBodyDocs2 = HashMap<String, List<RequestBody>>()
    var fileBodyList = ArrayList<HashMap<String, RequestBody>>()
    var files = ArrayList<MultipartBody.Part>()
    var multipleFile: HashMap<String, MultipartBody.Part> = HashMap()
    var multipleFileDriver: List<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var file: MultipartBody.Part? = null
}

// Extensions.kt
fun createPartFromString(stringData: String): RequestBody {
    return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
}