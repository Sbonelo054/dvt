package com.dvt.dvtapp.model

sealed class WeatherResults<T> {
    data class SuccessResults<T>(val data: T) : WeatherResults<T>()
    data class Error<T>(val error: Throwable) : WeatherResults<T>()
}
