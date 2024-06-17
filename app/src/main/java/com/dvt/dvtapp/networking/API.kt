package com.dvt.dvtapp.networking

import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.forecast.ForecastWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("forecast")
    suspend fun getForecast(@Query("q") place: String?, @Query("units") units: String, @Query("appid") key: String): ForecastWeather

    @GET("weather")
    suspend fun getCurrentWeather(@Query("q") place: String?, @Query("appid") key: String): CurrentWeather
}