package com.maxrave.simpweather

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var locationLabel: TextView? = null
    var statusLabel: TextView? = null
    var temperatureLabel: TextView? = null
    var humidityLabel: TextView? = null
    var realFeelsLabel: TextView? = null
    var statusImage: ImageView? = null
    var updateAtLabel: TextView? = null
    var temperatureDetailsLabel: TextView? = null
    var pressureLabel: TextView? = null
    var windLabel: TextView? = null
    var minTempLabel: TextView? = null
    var fab: FloatingActionButton? = null
    //View
    var mainContainer: View? = null
    var belowContainer: View? = null
    var pressureContainer: View? = null
    var windContainer: View? = null
    var minTempContainer: View? = null
    //var lat:Double = 0.0
    //var lon:Double = 0.0
    val weatherApi:String = "3515881a191de08773dc204279843606"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val  AUTOCOMPLETE_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
//        createCitiesList(listCities)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapping()
//        getWeather()
        var getData: Intent = getIntent()
        var lat = getData.getStringExtra("lat")
        var lon = getData.getStringExtra("lng")
        var cityName = getData.getStringExtra("city")
        var countryCode = getData.getStringExtra("country")
//        getLocation(this)?.let {
//            if (lat != null && lon != null) {
//                getCurrentWeather(lat, lon, cityName, countryCode)
//            } else {
//                getCurrentWeather(it.latitude.toString(), it.longitude.toString())
//            } }
        if (lat != null && lon != null) {
            getCurrentWeather(lat, lon, cityName, countryCode)
        } else {
            getLocation(this)?.let { getCurrentWeather(it.latitude.toString(), it.longitude.toString()) }
        }
        fab?.setOnClickListener() {
            val intent: Intent = Intent(this, SearchCityActivity::class.java)
            startActivity(intent)
        }
    }


    private fun fetchLocation(locationNow: MutableList<String>) {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                locationNow.add((it.latitude).toString())
                locationNow.add(it.longitude.toString())
            }
        }

    }


    private fun mapping(){
        locationLabel = findViewById(R.id.locationLabel)
        statusLabel = findViewById(R.id.statusLabel)
        temperatureLabel = findViewById(R.id.temperatureLabel)
        humidityLabel = findViewById(R.id.humidityLabel)
        realFeelsLabel = findViewById(R.id.realFeelLabel)
        statusImage = findViewById(R.id.statusImage)
        updateAtLabel = findViewById(R.id.updateAtLabel)
        mainContainer = findViewById(R.id.mainContainer)
        temperatureDetailsLabel = findViewById(R.id.temperatureDetailsLabel)
        fab = findViewById(R.id.fab)
        pressureLabel = findViewById(R.id.pressureLabel)
        windLabel = findViewById(R.id.windLabel)
        minTempLabel = findViewById(R.id.minTempLabel)
        belowContainer = findViewById(R.id.belowContainer)
        pressureContainer = findViewById(R.id.pressureContainer)
        windContainer = findViewById(R.id.windContainer)
        minTempContainer = findViewById(R.id.minTempContainer)
    }
    val REQUEST_CODE_LOCATION_PERMISSION = 100

    fun getLocation(context: Context): Location? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { requestPermissions(); return null }

        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        return mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) getWeather()
    }



    fun getWeather(){
        getLocation(this)?.let { getCurrentWeather(it.latitude.toString(),it.longitude.toString()) }
    }
    fun getCurrentWeather(lat: String, lon: String, cityName: String? = null, countryCode: String? = null){
        var locationNow: MutableList<String> = mutableListOf()
        locationNow.addAll(0, listOf(lat, lon))
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url:String = "https://api.openweathermap.org/data/2.5/weather?lat=${locationNow[0]}&lon=${locationNow[1]}&appid=${weatherApi}&units=metric"
        val stringRequest = StringRequest(com.android.volley.Request.Method.GET, url,
            { response ->
                var timeNow = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                var hour = timeNow.hour
                var myFormat = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
                var timeNowFormatted = timeNow.format(myFormat)
                Log.d("timeNow", timeNowFormatted)
                updateAtLabel?.text = "Updated at: ${timeNowFormatted}"
                Log.d("time", timeNow.toString())
                val jsonObj = JSONObject(response)
                //main
                val main = jsonObj.getJSONObject("main")
                val temp = main.getString("temp")
                val fixTemp = Math.round((temp.toDouble()*10)/10)
                temperatureLabel?.text = fixTemp.toString() + "??C"
                val humidity = main.getString("humidity") + "%"
                humidityLabel?.text = "Humidity: "+ humidity
                val feelsLike = main.getString("feels_like")
                val fixFeelsLike = Math.round((feelsLike.toDouble()*10)/10)
                realFeelsLabel?.text = "Real Feels: " + fixFeelsLike + "??C"
                //weather
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val statusId = weather.getString("id")
                val status = weather.getString("main")
                if (hour in 6..18) { //day
                    mainContainer?.setBackgroundResource(R.drawable.background_clear_sky)
                    belowContainer?.setBackgroundResource(R.drawable.background_clear_sky_reverse)
                    pressureContainer?.setBackgroundResource(R.drawable.background_clear_sky_reverse)
                    windContainer?.setBackgroundResource(R.drawable.background_clear_sky_reverse)
                    minTempContainer?.setBackgroundResource(R.drawable.background_clear_sky_reverse)
                    if (statusId.toInt() in 200..232) {
                        statusImage?.setImageResource(R.drawable.rainthunder)
                    } else if (statusId.toInt() in 300..321 && statusId.toInt() in 500..531) {
                        statusImage?.setImageResource(R.drawable.rainy)
                    } else if (statusId.toInt() in 600..622) {
                        statusImage?.setImageResource(R.drawable.snowy)
                    } else if (statusId.toInt() == 800) {
                        statusImage?.setImageResource(R.drawable.clear)
                    } else if (statusId.toInt() in 801..804) {
                        statusImage?.setImageResource(R.drawable.partly_cloudy)
                    }
                }
                else{ //night
                    mainContainer?.setBackgroundResource(R.drawable.background_night_sky)
                    belowContainer?.setBackgroundResource(R.drawable.background_night_sky_reverse)
                    pressureContainer?.setBackgroundResource(R.drawable.background_night_sky_reverse)
                    windContainer?.setBackgroundResource(R.drawable.background_night_sky_reverse)
                    minTempContainer?.setBackgroundResource(R.drawable.background_night_sky_reverse)
                    if (statusId.toInt() in 200..232) {
                        statusImage?.setImageResource(R.drawable.rainthunder_night)
                    } else if (statusId.toInt() in 300..321 && statusId.toInt() in 500..531) {
                        statusImage?.setImageResource(R.drawable.rainy_night)
                    } else if (statusId.toInt() in 600..622) {
                        statusImage?.setImageResource(R.drawable.snowy_night)
                    } else if (statusId.toInt() == 800) {
                        statusImage?.setImageResource(R.drawable.clear_night)
                    } else if (statusId.toInt() in 801..804) {
                        statusImage?.setImageResource(R.drawable.partly_cloudy_night)
                    }
                }
                statusLabel?.text = status
                val description = weather.getString("description")
                temperatureDetailsLabel?.text = description[0].uppercaseChar() + description.substring(1)
                //city
                val city = jsonObj.getString("name")
                val sys = jsonObj.getJSONObject("sys")
                val country = sys.getString("country")
                if (cityName == null && countryCode == null){
                    locationLabel?.text = "$city, $country"
                }
                else {
                    locationLabel?.text = "$cityName, $countryCode"
                }
                //bottom
                var minTemp:String = Math.round(((main.getString("temp_min").toDouble())*10)/10).toString()
                minTempLabel?.text = "$minTemp ??C"
                var winSpeed:String = jsonObj.getJSONObject("wind").getString("speed")
                windLabel?.text = "$winSpeed m/s"
                var pressure:String = main.getString("pressure")
                pressureLabel?.text = "$pressure hPa"
            },
            { error -> Log.d("Error.Response", error.toString())})
        requestQueue.add(stringRequest)
    }

}