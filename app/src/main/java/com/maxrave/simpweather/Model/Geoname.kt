package com.maxrave.simpweather.Model


import com.google.gson.annotations.SerializedName

data class Geoname(
    @SerializedName("geonames")
    val geonames: List<GeonameX>,
    @SerializedName("totalResultsCount")
    val totalResultsCount: Int
)