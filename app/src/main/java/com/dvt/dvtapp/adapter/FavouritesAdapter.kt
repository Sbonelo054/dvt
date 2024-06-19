package com.dvt.dvtapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvt.dvtapp.R
import com.dvt.dvtapp.database.FavouriteTable
import com.squareup.picasso.Picasso

class FavouritesAdapter(private val context: Context) :
    RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {
    var favourites: List<FavouriteTable> = ArrayList()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.FavouritesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.favourites_list, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouritesAdapter.FavouritesViewHolder, position: Int) {
        val favourite = favourites[position]
        when {
            favourite.description?.contains(context.resources.getString(R.string.cloud)) == true -> {
                Picasso.get().load(R.drawable.partlysunny_2x).into(holder.image)
            }

            favourite.description?.contains(context.resources.getString(R.string.rain)) == true -> {
                Picasso.get().load(R.drawable.rain_2x).into(holder.image)
            }

            else -> {
                Picasso.get().load(R.drawable.clear_2x).into(holder.image)
            }
        }
        val minTemp = "${favourite.maxTemp.take(2)}°/${favourite.minTemp.take(2)}°"
        holder.minMaxTemperature.text = minTemp
        holder.description.text = favourite.description
        holder.favouritePlace.text = favourite.place
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, favourite)
            }
        }
    }

    fun setData(favourites: List<FavouriteTable>) {
        this.favourites = favourites
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: FavouriteTable)
    }

    inner class FavouritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var minMaxTemperature: TextView = itemView.findViewById(R.id.favouriteTemperature)
        var favouritePlace: TextView = itemView.findViewById(R.id.favouritePlace)
        var description: TextView = itemView.findViewById(R.id.favouriteDescription)
        var image: ImageView = itemView.findViewById(R.id.favouriteImage)
    }
}