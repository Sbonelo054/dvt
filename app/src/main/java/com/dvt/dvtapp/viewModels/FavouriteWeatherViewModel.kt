package com.dvt.dvtapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.repository.FavouriteWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteWeatherViewModel(application: Application, private val repository: FavouriteWeatherRepository):
    AndroidViewModel(application) {

    fun addFavourite(favouriteTable: FavouriteTable) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFavourite(favouriteTable)
        }
    }

    fun getFavourites(): LiveData<List<FavouriteTable>>? {
        return repository.getFavourites()
    }
}