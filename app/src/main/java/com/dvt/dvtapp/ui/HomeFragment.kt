package com.dvt.dvtapp.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.dvtapp.R
import com.dvt.dvtapp.adapter.ForecastAdapter
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.databinding.FragmentHomeBinding
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.utils.Constants
import com.dvt.dvtapp.viewModels.FavouriteWeatherViewModel
import com.dvt.dvtapp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModel<WeatherViewModel>()
    private val favouriteWeatherViewModel by viewModel<FavouriteWeatherViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var favouriteTable: FavouriteTable
    private var alert : Dialog? = null
    private var adapter: ForecastAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), Constants.PLACES_ID)
        }
        Places.createClient(this.requireContext())
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    getLocation()
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
        return binding.root
    }
    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location!=null){
                val geocoder = Geocoder(requireContext(), Locale.getDefault())

                val addresses: List<Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city: String? = addresses?.get(0)?.locality
                //cityName = city
                fetchForecast(city.toString())
            }else{
                onSearch()
                Toast.makeText(context,"location is null",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ForecastAdapter()
        binding.WeatherRecyclerview.setHasFixedSize(true)
        binding.WeatherRecyclerview.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.WeatherRecyclerview.layoutManager = linearLayoutManager
    }

    private fun fetchForecast(place: String){
        viewModel.getForecast(place).observe(viewLifecycleOwner) { response ->
            val error = response as? WeatherResults.Error
            if (error != null) {
                connectionError(error.error)
            }

            val success = (response as? WeatherResults.SuccessResults)?.data
            success?.let {
                it.weatherList[0].main

                adapter?.setData(it.weatherList)
                val linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.WeatherRecyclerview.layoutManager = linearLayoutManager

                if (it.weatherList[0].weather[0].main.toString().contains("Cloud")) {
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                   binding.imageView.setImageResource(R.drawable.forest_cloudy)

                } else if (it.weatherList[0].weather[0].main.toString().contains("Rain")) {
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                    binding.imageView.setImageResource(R.drawable.forest_rainy)

                } else if (it.weatherList[0].weather[0].main.toString().contains("Sun")){
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                    binding.imageView.setImageResource(R.drawable.forest_sunny)
                } else{
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                    binding.imageView.setImageResource(R.drawable.forest_sunny)
                }
            }
        }

        viewModel.getCurrentWeather(place).observe(viewLifecycleOwner){ response ->
            val error = response as? WeatherResults.Error
            if (error != null) {
                connectionError(error.error)
            }

            val success = (response as? WeatherResults.SuccessResults)?.data
            success?.let {
                binding.description.text = it.weather[0].main
                binding.temperature.text = it.main?.temp.toString().take(2) + " °\n"
                binding.currentTemp.text = it.main?.temp.toString().take(2) + " °\n" + "Current "
                binding.minTemp.text = it.main?.tempMin.toString().take(2) + " °\n" + "min "
                binding.maxTemp.text = it.main?.tempMax.toString().take(2) + " °\n" + "max "
                favouriteTable = FavouriteTable(it.name.toString(),it.main?.tempMin.toString(),it.main?.tempMax.toString(),it.weather[0].description)
            }
        }
    }

     fun connectionError(throwable: Throwable) {
        val showing = alert?.isShowing ?: false
        if(showing)
            return
        val message= throwable.toString()

        val title: String
        val content: String
        when {
            message.contains("java.net.UnknownHostException",true) -> {
                title =  "Internet Not Available"
                content = "Could not connect to the Internet. Please verify that you are connected and try again"
            }
            message.contains("java.net.SocketTimeoutException",true) -> {
                title =  "Connection Timeout"
                content = "Server took too long to respond. This may be caused by a bad network connection"
            }
            message.contains("javax.net.ssl.SSLPeerUnverifiedException", true) -> {
                title = "SSL Cert. Unverified"
                content = "Hostname not verified"
            }
            else -> {
                title = "Unknown Error"
                content = "An Unknown error has occurred. Please try again later"
            }
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton("Retry") { dialog: DialogInterface?, _: Int ->
                getHistory()
                dialog?.dismiss()
            }

        alert = builder.create()
        alert?.show()
    }

    private fun getHistory(){

    }

    private fun onSearch(){
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this.requireContext())
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                if(place.name != null){

                    place.name?.let { fetchForecast(it) }
                    findNavController().navigateUp()
                }
            }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
                val status= Autocomplete.getStatusFromIntent(data)
                Toast.makeText(context,status.statusMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return inflater.inflate(R.menu.menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_city -> {
                onSearch()
                true
            }

            R.id.menu_add_favourite -> {
                favouriteWeatherViewModel.addFavourite(favouriteTable)
                findNavController().navigateUp()
                true
            }

            R.id.menu_favourites -> {
                findNavController().navigate(R.id.home_to_favourites)
                true
            }

            R.id.menu_maps -> {
                findNavController().navigate(R.id.home_to_maps)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}