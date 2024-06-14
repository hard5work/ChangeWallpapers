package com.xdroid.app.changewallpaper.data.neworks

import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable


interface ApiHelper {
    suspend fun postMethod(
        url: String,
        headers: HashMap<String, String>,
        body: JsonObject
    ): Observable<JsonObject>

    suspend fun getMethod(
        url: String,
        headers: HashMap<String, String>
    ): Observable<JsonObject>

    suspend fun putMethod(
        url: String, headers: HashMap<String, String>,
        body: HashMap<String, String>
    ): Observable<JsonObject>

    suspend fun patchMethod(
        url: String, headers: HashMap<String, String>,
        body: HashMap<String, String>
    ): Observable<JsonObject>

    suspend fun deleteMethod(
        url: String,
        headers: HashMap<String, String>
    ): Observable<JsonObject>


}