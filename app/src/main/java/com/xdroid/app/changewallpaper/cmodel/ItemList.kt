package com.xdroid.app.changewallpaper.cmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ItemModel(
    val page: Long? = null,
    val perPage: Long? = null,
    val totalItems: Long? = null,
    val totalPages: Long? = null,
    val items: List<Item>? = null
) : java.io.Serializable


data class Item(
    val category: String? = null,

    @SerializedName("collectionId")
    val collectionID: String? = null,

    val collectionName: String? = null,
    val created: String? = null,
    val id: String? = null,
    val image: String? = null,
    val images: List<String>? = null,
    val name: String? = null,
    val updated: String? = null
) : java.io.Serializable


data class MyItems(
    @SerializedName("collectionId")
    val collectionID: String? = null,

    val id: String? = null,
    val image: String? = null,
    val created: String? = null,

    ) : Serializable

data class ListItems(
    val items: List<MyItems>
) : Serializable
