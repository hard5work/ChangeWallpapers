package com.xdroid.app.changewallpaper.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xdroid.app.changewallpaper.cmodel.MyItems
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

@Dao
interface MyItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MyItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MyItems>)

    @Query("SELECT * FROM my_items")
    fun getAllItems():Flow<List<MyItems>>

    @Query("SELECT * FROM my_items WHERE id = :id")
    suspend fun getItemById(id: String): MyItems?

    @Delete
    suspend fun deleteItem(item: MyItems)

    @Query("DELETE FROM my_items")
    suspend fun clearAll()
}