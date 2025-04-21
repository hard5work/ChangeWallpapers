package com.xdroid.app.changewallpaper.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<Favorites>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: Favorites)

    @Delete
    suspend fun delete(fav: Favorites)

    @Query("SELECT * FROM favorites WHERE url = :content LIMIT 1")
    fun getImageByIdOrContent(content: String): Flow<Favorites?>
}