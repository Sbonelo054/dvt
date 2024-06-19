package com.dvt.dvtapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.database.WeatherDao
import com.dvt.dvtapp.database.WeatherDatabase

class FavouriteWeatherRepositoryImpl(application: Application) : FavouriteWeatherRepository {
    private lateinit var dao: WeatherDao

    init {
        val database = WeatherDatabase.getInstance(application)
        if (database != null) {
            dao = database.weatherDao()
        }
    }

    override suspend fun addFavourite(favouriteTable: FavouriteTable) {
        dao.addFavouriteWeather(favouriteTable)
    }

    override fun getFavourites(): LiveData<List<FavouriteTable>>? {
        return dao.getFavorites()
    }
}