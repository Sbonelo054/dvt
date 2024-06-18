package com.dvt.dvtapp.dependencyInjection

import com.dvt.dvtapp.viewModels.FavouriteWeatherViewModel
import com.dvt.dvtapp.viewModels.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        WeatherViewModel(get())
    }

    viewModel {
        FavouriteWeatherViewModel(get())
    }
}