package com.maxrave.simpweather.Model

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxrave.simpweather.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class GeonameAdapter(private val listCities: ArrayList<GeonameX>): RecyclerView.Adapter<GeonameAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val addressSearchText: TextView = itemView.findViewById(R.id.addressSearchText)
        val flagImage: ImageView = itemView.findViewById(R.id.flagImage)


    }
    var onItemClick: ((GeonameX) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cities, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city: GeonameX = listCities[position]
        holder.addressSearchText.text = "${city.name}, ${city.adminName1}, ${city.countryName}"
//        Picasso.get().load("https://countryflagsapi.com/png/${city.countryCode}").into(holder.flagImage)
        Glide.with(holder.itemView.context).load("https://countryflagsapi.com/png/${city.countryCode}")
            .centerCrop()
            .placeholder(R.drawable.vn)
            .into(holder.flagImage)

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(city)
        }
    }

    override fun getItemCount(): Int {
        return listCities.size
    }

}