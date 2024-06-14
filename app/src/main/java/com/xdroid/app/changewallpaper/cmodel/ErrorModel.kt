package com.xdroid.app.changewallpaper.cmodel

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("status")
    val title: String,
    val message: String
)