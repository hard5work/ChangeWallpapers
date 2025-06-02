package com.xdroid.app.changewallpaper.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xdroid.app.changewallpaper.cmodel.MyItems

@Database(entities = [Favorites::class, MyItems::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favDao(): FavoriteDao
    abstract fun myItemsDao(): MyItemsDao
}