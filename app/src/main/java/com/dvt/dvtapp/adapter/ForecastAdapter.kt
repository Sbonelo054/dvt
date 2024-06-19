package com.dvt.dvtapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dvt.dvtapp.R
import com.dvt.dvtapp.model.forecast.WeatherList
import com.dvt.dvtapp.utils.Constants
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class ForecastAdapter(private val context: Context): RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private var forecast: List<WeatherList> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_list, parent, false)
        return ForecastViewHolder(view)
    }
    override fun getItemCount(): Int {
        return forecast.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val dayForecast = forecast[position]
        holder.temperatureView.text = "${dayForecast.main?.temp.toString().take(2)}°"
        val parser = SimpleDateFormat("yyyy-mm-dd HH:mm")
        val formatter = SimpleDateFormat("EEEE HH:mm")
        val resultDate = formatter.format(parser.parse(dayForecast.dtTxt.toString()) as Date)
        holder.dateView.text = resultDate
        val description = forecast[0].weather[0].main.toString()
        when {
            description.contains(context.resources.getString(R.string.cloud)) -> { Picasso.get().load(R.drawable.partlysunny_2x).into(holder.imageView)
            }
            description.contains(context.resources.getString(R.string.rain)) -> {
                Picasso.get().load(R.drawable.rain_2x).into(holder.imageView)
            }
            description.contains(context.resources.getString(R.string.sun)) -> {
                Picasso.get().load(R.drawable.clear_2x).into(holder.imageView)
            }
            else -> {
                Picasso.get().load(R.drawable.clear_2x).into(holder.imageView)
            }
        }
    }

    fun setData(daysForecast: List<WeatherList>) {
        forecast = daysForecast
        notifyDataSetChanged()
    }

    inner class ForecastViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var dateView: TextView = itemView.findViewById(R.id.day_item)
        var temperatureView: TextView = itemView.findViewById(R.id.temperature_item)
        var imageView: ImageView = itemView.findViewById(R.id.image_item)
    }
}