package com.maxrave.simpweather.Model


import com.google.gson.annotations.SerializedName

data class GeonameX(
    @SerializedName("adminName1")
    val adminName1: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("countryName")
    val countryName: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("population")
    val population: Int,
    @SerializedName("toponymName")
    val toponymName: String
)