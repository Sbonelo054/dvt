package com.dvt.dvtapp.repository

import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.focast.FocastWeather

interface WeatherRepository {
    suspend fun getForecast(place : String?, unit : String) : WeatherResults<FocastWeather>

    suspend fun getCurrentWeather(place: String?) : WeatherResults<CurrentWeather>
}