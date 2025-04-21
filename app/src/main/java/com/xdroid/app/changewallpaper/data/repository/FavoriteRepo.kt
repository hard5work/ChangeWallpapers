package com.xdroid.app.changewallpaper.data.repository

import com.xdroid.app.changewallpaper.data.room.FavoriteDao
import com.xdroid.app.changewallpaper.data.room.Favorites
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {
    val notes = dao.getAllFavorites()

    suspend fun insert(note: Favorites) = dao.insert(note)
    suspend fun delete(note: Favorites) = dao.delete(note)
//    suspend fun getSingleItem(imageID: String, content: String) =
//        dao.getImageByIdOrContent(imageID = imageID, content = content)

    fun getSingleItem(content: String): Flow<Favorites?> {
        return dao.getImageByIdOrContent(content)
    }


}