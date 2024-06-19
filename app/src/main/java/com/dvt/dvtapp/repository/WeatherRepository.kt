package com.dvt.dvtapp.repository

import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.forecast.ForecastWeather

interface WeatherRepository {
    suspend fun getForecast(place: String?, unit: String): WeatherResults<ForecastWeather>

    suspend fun getCurrentWeather(place: String?): WeatherResults<CurrentWeather>
}