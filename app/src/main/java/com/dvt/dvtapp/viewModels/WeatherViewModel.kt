package com.dvt.dvtapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.forecast.ForecastWeather
import com.dvt.dvtapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

    fun getForecast(place: String?, unit: String): MutableLiveData<WeatherResults<ForecastWeather>> {
        val weatherData: MutableLiveData<WeatherResults<ForecastWeather>> = MutableLiveData()
        viewModelScope.launch {
            val results = weatherRepository.getForecast(place, unit)
            weatherData.value = results
        }
        return weatherData
    }

    fun getCurrentWeather(place: String?): MutableLiveData<WeatherResults<CurrentWeather>> {
        val weatherData: MutableLiveData<WeatherResults<CurrentWeather>> = MutableLiveData()
        viewModelScope.launch {
            val results = weatherRepository.getCurrentWeather(place)
            weatherData.value = results
        }
        return weatherData
    }
}