package com.dvt.dvtapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.model.current.CurrentWeather
import com.dvt.dvtapp.model.focast.FocastWeather
import com.dvt.dvtapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

    fun getForecast(place: String?): MutableLiveData<WeatherResults<FocastWeather>> {
        val weatherData: MutableLiveData<WeatherResults<FocastWeather>> = MutableLiveData()
        viewModelScope.launch {
            val results = weatherRepository.getForecast(place, unit = "metric")
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