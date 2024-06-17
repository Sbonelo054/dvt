package com.dvt.dvtapp.repository

import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.forecast.ForecastWeather
import com.dvt.dvtapp.networking.API
import com.dvt.dvtapp.networking.Client
import com.dvt.dvtapp.utils.Constants

class WeatherRepositoryImpl: WeatherRepository {
    private lateinit var api: API

    override suspend fun getForecast(place: String?, unit: String): WeatherResults<ForecastWeather> {
        api = Client.instance.create(API::class.java)
        return try {
            val results = api.getForecast(place,unit, Constants.APP_ID)
            WeatherResults.SuccessResults(results)
        } catch (t : Throwable) {
            WeatherResults.Error(t)
        }
    }

    override suspend fun getCurrentWeather(place: String?): WeatherResults<CurrentWeather> {
        api = Client.instance.create(API::class.java)
        return try {
            val results = api.getCurrentWeather(place, Constants.APP_ID)
            WeatherResults.SuccessResults(results)
        } catch (t : Throwable) {
            WeatherResults.Error(t)
        }
    }
}