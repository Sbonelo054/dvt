package com.dvt.dvtapp.dependencyInjection

import com.dvt.dvtapp.repository.FavouriteWeatherRepository
import com.dvt.dvtapp.repository.FavouriteWeatherRepositoryImpl
import com.dvt.dvtapp.repository.WeatherRepository
import com.dvt.dvtapp.repository.WeatherRepositoryImpl
import org.koin.dsl.module

val repoModule = module {
    single<WeatherRepository> {
        WeatherRepositoryImpl()
    }

    single<FavouriteWeatherRepository> {
        FavouriteWeatherRepositoryImpl(application = get())
    }
}