package com.xdroid.app.changewallpaper.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorites(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String
)