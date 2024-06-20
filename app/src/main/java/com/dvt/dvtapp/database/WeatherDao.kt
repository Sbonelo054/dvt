package com.dvt.dvtapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert
    suspend fun addFavouriteWeather(favouriteTable: FavouriteTable)

    @Query("SELECT * FROM favourite")
    fun getFavorites(): LiveData<List<FavouriteTable>>?
}