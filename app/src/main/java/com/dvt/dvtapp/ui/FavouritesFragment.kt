package com.dvt.dvtapp.ui

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dvt.dvtapp.MainActivity
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
        val description = arguments?.getString(getString(R.string.description))
        fetchFavouriteWeather(description.toString())
    }

    private fun fetchFavouriteWeather(description: String) {
        favouriteWeatherViewModel.getFavourites()?.observe(viewLifecycleOwner) {
            if (it != null) {
                if (description.contains(getString(R.string.cloud))) {
                    val window = (requireActivity() as MainActivity).window
                    window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.cloudy_color)
                    (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.cloudyStatusBar))))
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cloudy_color))
                } else if (description.contains(getString(R.string.rain))) {
                    val window = (requireActivity() as MainActivity).window
                    window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.rainy_color)
                    (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.rainyStatusBar))))
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rainy_color))
                } else {
                    val window = (requireActivity() as MainActivity).window
                    window.statusBarColor = (requireActivity() as MainActivity).resources.getColor(R.color.sunny_color)
                    (requireActivity() as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getString(R.string.sunnyStatusBar))))
                    binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sunny_color))
                }
                adapter = FavouritesAdapter(requireContext())
                binding.favouriteRecyclerview.setHasFixedSize(true)
                binding.favouriteRecyclerview.adapter = adapter
                adapter?.setData(it)
                val linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.favouriteRecyclerview.layoutManager = linearLayoutManager
                adapter?.setOnClickListener(object :
                    FavouritesAdapter.OnClickListener {
                    override fun onClick(position: Int, model: FavouriteTable) {
                        val bundle = bundleOf(getString(R.string.place) to model.place, getString(R.string.description) to getString(R.string.description))
                        findNavController().navigate(
                            R.id.favourites_list_to_favourites_details_fragment, bundle
                        )
                    }
                })
            }
        }
    }
}