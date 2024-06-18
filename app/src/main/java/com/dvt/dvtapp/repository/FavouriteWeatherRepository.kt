package com.dvt.dvtapp.repository

import androidx.lifecycle.LiveData
import com.dvt.dvtapp.database.FavouriteTable

interface FavouriteWeatherRepository {
    suspend fun addFavourite(favouriteTable: FavouriteTable)

    fun getFavourites(): LiveData<List<FavouriteTable>>?
}