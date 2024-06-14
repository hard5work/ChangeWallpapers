package com.xdroid.app.changewallpaper.data.neworks

import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun postMethod(
        url: String,
        headers: HashMap<String, String>,
        body: JsonObject
    ): Observable<JsonObject> = apiService.postMethod(url, headers, body)

    override suspend fun getMethod(
        url: String,
        headers: HashMap<String, String>
    ): Observable<JsonObject> = apiService.getMethod(url, headers)

    override suspend fun putMethod(
        url: String,
        headers: HashMap<String, String>,
        body: HashMap<String, String>
    ): Observable<JsonObject> = apiService.putMethod(url, headers, body)

    override suspend fun patchMethod(
        url: String,
        headers: HashMap<String, String>,
        body: HashMap<String, String>
    ): Observable<JsonObject> = apiService.patchMethod(url, headers, body)

    override suspend fun deleteMethod(
        url: String,
        headers: HashMap<String, String>
    ): Observable<JsonObject> = apiService.deleteMethod(url, headers)

}