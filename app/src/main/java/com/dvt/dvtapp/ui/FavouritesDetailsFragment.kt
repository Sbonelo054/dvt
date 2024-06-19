package com.dvt.dvtapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dvt.dvtapp.R
import com.dvt.dvtapp.databinding.FragmentFavouritesBinding
import com.dvt.dvtapp.databinding.FragmentFavouritesDetailsBinding
import com.dvt.dvtapp.viewModels.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavouritesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesDetailsBinding
    private val weatherViewModel by viewModel<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavouritesDetailsBinding.inflate(inflater, container, false)
        val place = arguments?.getString(getString(R.string.place))
        weatherViewModel.getCurrentWeather(place).observe(viewLifecycleOwner) {

        }
        return binding.root
    }

}