package com.xdroid.app.changewallpaper.data.neworks

import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface ApiService {

    @POST()
    fun postMethod(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body jsonObject: JsonObject
    ): Observable<JsonObject>

    @GET()
    fun getMethod(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Observable<JsonObject>


    @FormUrlEncoded
    @PUT()
    fun putMethod(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @FieldMap body: HashMap<String, String>
    ): Observable<JsonObject>


    @PATCH()
    fun patchMethod(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @FieldMap body: HashMap<String, String>
    ): Observable<JsonObject>


    @DELETE()
    fun deleteMethod(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Observable<JsonObject>

}