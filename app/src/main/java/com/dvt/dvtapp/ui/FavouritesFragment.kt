package com.dvt.dvtapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dvt.dvtapp.R
import com.dvt.dvtapp.adapter.FavouritesAdapter
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.databinding.FragmentFavouritesBinding
import com.dvt.dvtapp.viewModels.FavouriteWeatherViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding
    private val favouriteWeatherViewModel by viewModel<FavouriteWeatherViewModel>()
    private var adapter: FavouritesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val description = arguments?.getString("description")
        fetchFavouriteWeather(description.toString())
    }

    private fun fetchFavouriteWeather(description: String){
        favouriteWeatherViewModel.getFavourites()?.observe(viewLifecycleOwner){
            if (it != null) {
                    if (description.contains(getString(R.string.cloud))) {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                    } else if (description.contains(getString(R.string.rain))) {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                    } else {
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                    }
                adapter = FavouritesAdapter()
                binding.favouriteRecyclerview.setHasFixedSize(true)
                binding.favouriteRecyclerview.adapter = adapter
                adapter?.setData(it)
                val linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.favouriteRecyclerview.layoutManager = linearLayoutManager
                adapter?.setOnClickListener(object :
                    FavouritesAdapter.OnClickListener {
                    override fun onClick(position: Int, model: FavouriteTable) {
                        val bundle = bundleOf(getString(R.string.place) to model.place, "description" to "description")
                        findNavController().navigate(R.id.favourites_list_to_favourites_details_fragment, bundle
                        )
                    }
                })
            }
        }
    }
}