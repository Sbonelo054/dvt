package com.dvt.dvtapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.dvt.dvtapp.MainActivity
import com.dvt.dvtapp.R
import com.dvt.dvtapp.databinding.FragmentFavouritesBinding
import com.dvt.dvtapp.databinding.FragmentFavouritesDetailsBinding
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.viewModels.WeatherViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavouritesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesDetailsBinding
    private val weatherViewModel by viewModel<WeatherViewModel>()
    private var alert: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavouritesDetailsBinding.inflate(inflater, container, false)
        val place = arguments?.getString(getString(R.string.place))
        fetchWeather(place.toString())
        return binding.root
    }

    private fun fetchWeather(place: String){
        weatherViewModel.getForecast(place, unit = getString(R.string.metric)).observe(viewLifecycleOwner) { response->
            val error = response as? WeatherResults.Error
            if (error != null) {
                connectionError(error.error)
            }
            val success = (response as? WeatherResults.SuccessResults)?.data
            success?.let {
                val description = it.weatherList[0].weather[0].main
                val temperature = "${it.weatherList[0].main?.temp.toString().take(2)} °"
                when {
                    description.toString().contains(getString(R.string.cloud)) -> {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                        Picasso.get().load(R.drawable.clear_2x).into(binding.favouriteDetailsIcon)
                    }
                    description.toString().contains(getString(R.string.rain)) -> {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                        Picasso.get().load(R.drawable.clear_2x).into(binding.favouriteDetailsIcon)
                    }
                    else -> {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                        Picasso.get().load(R.drawable.clear_2x).into(binding.favouriteDetailsIcon)
                    }
                }
                val visibilityData = "${it.weatherList[0].visibility.toString()} ft"
                val pressureData = "${it.weatherList[0].main?.pressure.toString()} hPa"
                val seaLevelData =  "${it.weatherList[0].main?.grndLevel.toString()} hPa"
                val humidityData = "${it.weatherList[0].main?.humidity.toString()} %"
                val windData = "${it.weatherList[0].wind?.deg.toString()} °"
                val cloudsData = "${it.weatherList[0].clouds?.all.toString()} %"

                binding.favouriteDetailsDescription.text = description
                binding.favouriteDetailsTemperature.text = temperature
                binding.favouriteDetailsCity.text = place
                binding.favouriteDetailsCurrentDate.text = getString(R.string.today)
                binding.favouriteDetailsClouds.text = cloudsData
                binding.favouriteDetailsWind.text = windData
                binding.favouriteDetailsPressure.text = pressureData
                binding.favouriteDetailsSeaLevel.text = seaLevelData
                binding.favouriteDetailsHumidity.text = humidityData
                binding.favouriteDetailsVisibility.text = visibilityData

                val firstTempData = "${it.weatherList[1].main?.tempMax?.toInt().toString()} °"
                val secondTempData = "${it.weatherList[2].main?.tempMax?.toInt().toString()} °"
                val thirdTempData = "${it.weatherList[3].main?.tempMax?.toInt().toString()} °"
                val fourthTempData = "${it.weatherList[4].main?.tempMax?.toInt().toString()} °"

                binding.favouriteDetailsFirstTemperature.text = firstTempData
                binding.favouriteDetailsSecondTemperature.text = secondTempData
                binding.favouriteDetailsThirdTemperature.text = thirdTempData
                binding.favouriteDetailsForthTemperature.text = fourthTempData

                binding.favouriteDetailsFirstTime.text = it.weatherList[1].dtTxt?.substring(10, 16)
                binding.favouriteDetailsSecondTime.text = it.weatherList[2].dtTxt?.substring(10, 16)
                binding.favouriteDetailsThirdTime.text = it.weatherList[3].dtTxt?.substring(10, 16)
                binding.favouriteDetailsForthTime.text = it.weatherList[4].dtTxt?.substring(10, 16)
            }
        }
    }

    private fun connectionError(throwable: Throwable) {
        val showing = alert?.isShowing ?: false
        if (showing)
            return
        val message = throwable.toString()

        val title: String
        val content: String
        when {
            message.contains(getString(R.string.java_net_unknownhostexception), true) -> {
                title = getString(R.string.internet_not_available)
                content =
                    getString(R.string.could_not_connect_to_the_internet_please_verify_that_you_are_connected_and_try_again)
            }

            message.contains(getString(R.string.java_net_sockettimeoutexception), true) -> {
                title = getString(R.string.connection_timeout)
                content =
                    getString(R.string.server_took_too_long_to_respond_this_may_be_caused_by_a_bad_network_connection)
            }

            message.contains(
                getString(R.string.javax_net_ssl_sslpeerunverifiedexception),
                true
            ) -> {
                title = getString(R.string.ssl_cert_unverified)
                content = getString(R.string.hostname_not_verified)
            }

            else -> {
                title = getString(R.string.unknown_error)
                content = getString(R.string.an_unknown_error_has_occurred_please_try_again_later)
            }
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.retry)) { dialog: DialogInterface?, _: Int ->
                findNavController().navigateUp()
                dialog?.dismiss()
            }
        alert = builder.create()
        alert?.show()
    }

}