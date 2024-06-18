package com.dvt.dvtapp.database

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

interface WeatherDao {
    @Insert
    suspend fun addFavouriteWeather(table: FavouriteTable)

    @Query("SELECT * FROM favourite")
    fun getFavorites(): LiveData<List<FavouriteTable>>?
}