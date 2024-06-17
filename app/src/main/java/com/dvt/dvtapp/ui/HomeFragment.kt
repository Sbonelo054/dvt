package com.dvt.dvtapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.dvtapp.R
import com.dvt.dvtapp.adapter.ForecastAdapter
import com.dvt.dvtapp.databinding.FragmentHomeBinding
import com.dvt.dvtapp.model.WeatherResults
import com.dvt.dvtapp.utils.Constants
import com.dvt.dvtapp.viewModels.WeatherViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModel<WeatherViewModel>()
    private var alert : Dialog? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: ForecastAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), Constants.PLACES_ID)
        }
        Places.createClient(this.requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ForecastAdapter()
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
        fetchForecast()
    }

    fun fetchForecast(){
        viewModel.getForecast("Durban").observe(viewLifecycleOwner) { response ->
            val error = response as? WeatherResults.Error
            if (error != null) {
                connectionError(error.error)
            }

            val success = (response as? WeatherResults.SuccessResults)?.data
            success?.let {
                it.weatherList[0].main

                adapter?.setData(it.weatherList)
                val linearLayoutManager = LinearLayoutManager(requireActivity())
                recyclerView?.layoutManager = linearLayoutManager

                if (it.weatherList[0].weather[0].main.toString().contains("Cloud")) {
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                    binding.imageView.setImageResource(R.drawable.forest_cloudy)

                } else if (it.weatherList[0].weather[0].main.toString().contains("Rain")) {
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                    binding.imageView.setImageResource(R.drawable.forest_rainy)

                } else if (it.weatherList[0].weather[0].main.toString().contains("Sun")){
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                    binding.imageView.setImageResource(R.drawable.forest_sunny)
                }
            }
        }

        viewModel.getCurrentWeather("Durban").observe(viewLifecycleOwner){ response ->
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_city -> {
                onSearch()
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

            else -> false
        }
    }

    fun getWeather(){

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
                    //val locations = LocationTable(place.name!!)
                    //locationsViewModel.saveLocation(locations)
                    findNavController().navigateUp()
                }
            }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
                val status= Autocomplete.getStatusFromIntent(data)
                Toast.makeText(context,status.statusMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}