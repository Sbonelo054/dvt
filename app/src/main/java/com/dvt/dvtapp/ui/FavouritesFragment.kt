package com.dvt.dvtapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dvt.dvtapp.R
import com.dvt.dvtapp.adapter.FavouritesAdapter
import com.dvt.dvtapp.database.FavouriteTable
import com.dvt.dvtapp.databinding.FragmentFavouritesBinding
import com.dvt.dvtapp.viewModels.FavouriteWeatherViewModel
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
        fetchFavouriteWeather()
    }

    private fun fetchFavouriteWeather(){
        favouriteWeatherViewModel.getFavourites()?.observe(viewLifecycleOwner){
            if (it != null) {
                adapter = FavouritesAdapter(requireContext())
                binding.favouriteRecyclerview.setHasFixedSize(true)
                binding.favouriteRecyclerview.adapter = adapter
                adapter?.setData(it)
                val linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.favouriteRecyclerview.layoutManager = linearLayoutManager
                adapter?.setOnClickListener(object :
                    FavouritesAdapter.OnClickListener {
                    override fun onClick(position: Int, model: FavouriteTable) {
                        val bundle = bundleOf("place" to model.place)
                        findNavController().navigate(R.id.favourites_list_to_favourites_details_fragment, bundle
                        )
                    }
                })
            }
        }
    }


}