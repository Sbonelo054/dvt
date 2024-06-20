package com.dvt.dvtapp.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dvt.dvtapp.MainActivity
import com.dvt.dvtapp.R
import com.dvt.dvtapp.adapter.ForecastAdapter
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.databinding.FragmentHomeBinding
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.utils.Constants
import com.dvt.dvtapp.viewModels.FavouriteWeatherViewModel
import com.dvt.dvtapp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.koin.android.ext.android.inject
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: WeatherViewModel by inject()
    private val favouriteWeatherViewModel: FavouriteWeatherViewModel by inject()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private lateinit var favouriteTable: FavouriteTable
    private var alert: Dialog? = null
    private var adapter: ForecastAdapter? = null
    private var dialog: ProgressDialog? = null
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), Constants.PLACES_ID)
        }
        Places.createClient(this.requireContext())
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
        val window = (requireActivity() as MainActivity).window
        window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.sunny_color)
        (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.sunnyStatusBar))))
        return binding.root
    }

    fun requestLocation() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(30)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
            }
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            setPlaceFromCoordinates(location)
        }
    }

    private fun setPlaceFromCoordinates(location: Location?) {
        if (location != null) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val city: String? = addresses?.get(0)?.locality
            fetchForecast(city.toString())
        } else {
            //dialog = ProgressDialog.show(context, "Loading", "Please wait...", true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ForecastAdapter(requireContext())
        binding.WeatherRecyclerview.setHasFixedSize(true)
        binding.WeatherRecyclerview.adapter = adapter
        requestLocation()
        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    requestLocation()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?,
                    permissionToken: PermissionToken?
                ) {
                    permissionToken?.continuePermissionRequest()
                }

            }).check()
        setPlaceFromCoordinates(currentLocation)
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.WeatherRecyclerview.layoutManager = linearLayoutManager
    }

    private fun fetchForecast(place: String) {
       // dialog = ProgressDialog.show(context, "Loading", "Please wait...", true)
        viewModel.getForecast(place, unit = getString(R.string.metric))
            .observe(viewLifecycleOwner) { response ->
                //dialog?.hide()
                val error = response as? WeatherResults.Error
                if (error != null) {
                    connectionError(error.error)
                }
                val window = (requireActivity() as MainActivity).window
                val success = (response as? WeatherResults.SuccessResults)?.data
                success?.let {
                    description = it.weatherList[0].weather[0].main.toString()
                    binding.description.text = description
                    adapter?.setData(it.weatherList)
                    val linearLayoutManager = LinearLayoutManager(requireActivity())
                    binding.WeatherRecyclerview.layoutManager = linearLayoutManager
                    when {
                        description.contains(getString(R.string.cloud)) -> {
                            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                            (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.cloudyStatusBar))))
                            window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.cloudy_color)
                            binding.imageView.setImageResource(R.drawable.forest_cloudy)
                        }

                        description.toString().contains(getString(R.string.rain)) -> {
                            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                            window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.rainy_color)
                            (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.rainyStatusBar))))
                            binding.imageView.setImageResource(R.drawable.forest_rainy)
                        }

                        description.toString().contains(getString(R.string.sun)) -> {
                            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                            window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.sunny_color)
                            (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.sunnyStatusBar))))
                            binding.imageView.setImageResource(R.drawable.forest_sunny)
                        }

                        else -> {
                            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                            window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.sunny_color)
                            (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.sunnyStatusBar))))
                            binding.imageView.setImageResource(R.drawable.forest_sunny)
                        }
                    }
                }
            }
        viewModel.getCurrentWeather(place).observe(viewLifecycleOwner) { response ->

            val error = response as? WeatherResults.Error
            if (error != null) {
                connectionError(error.error)
            }

            val success = (response as? WeatherResults.SuccessResults)?.data
            success?.let {
                val temperature = "${it.main?.temp?.toInt().toString().take(2)}°"
                val currentTemp = it.main?.temp?.toInt().toString().take(2) + " °\n" + getString(R.string.current)
                val minTemp = it.main?.tempMin?.toInt().toString().take(2) + " °\n" + getString(R.string.min)
                val maxTemp = it.main?.tempMax?.toInt().toString().take(2) + " °\n" + getString(R.string.max)
                binding.temperature.text = temperature
                binding.currentTemp.text = currentTemp
                binding.minTemp.text = minTemp
                binding.maxTemp.text = maxTemp
                favouriteTable = FavouriteTable(
                    it.name.toString(),
                    it.main?.tempMin.toString(),
                    it.main?.tempMax.toString(),
                    it.weather[0].main
                )
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
            .setPositiveButton("Exit") { dialog: DialogInterface?, _: Int ->
               onDestroy()
                dialog?.dismiss()
            }
        alert = builder.create()
        alert?.show()
    }

    private fun onSearch() {
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this.requireContext())
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                if (place.name != null) {
                    place.name?.let { fetchForecast(it) }
                    findNavController().navigateUp()
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data)
                Toast.makeText(context, status.statusMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_city -> {
                onSearch()
                true
            }

            R.id.menu_add_favourite -> {
                favouriteWeatherViewModel.addFavourite(favouriteTable)
                Snackbar.make(binding.root, "Successfully added as favourite", Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
                true
            }

            R.id.menu_favourites -> {
                val bundle = bundleOf("description" to description)
                findNavController().navigate(R.id.home_to_favourites, bundle)
                true
            }

            R.id.menu_maps -> {
                findNavController().navigate(R.id.home_to_map_fragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

/*    override fun onResume() {
        super.onResume()
        dialog?.hide()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}