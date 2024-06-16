package com.dvt.dvtapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dvt.dvtapp.R
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
    private val viewModel by viewModel<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), Constants.PLACES_ID)
        }
        Places.createClient(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lblLabel = view.findViewById(R.id.current_temp) as TextView
        val image = view.findViewById(R.id.imageView) as ImageView
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