package com.maxrave.simpweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.maxrave.simpweather.Model.GeonameAdapter
import com.maxrave.simpweather.Model.GeonameX
import org.json.JSONObject

class SearchCityActivity : AppCompatActivity() {
    var searchCityView: SearchView? = null
    var listCityView: RecyclerView? = null
    lateinit var listCity : ArrayList<GeonameX>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_city)
        supportActionBar?.hide()
        mapping()
        val requestQueue = Volley.newRequestQueue(this)

        searchCityView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query == null) {
                    Toast.makeText(this@SearchCityActivity,
                        "Please enter any location",
                        Toast.LENGTH_SHORT).show()
                } else {
                    listCity.clear()
                    lateinit var queryProcessed: String
                    if(query.contains(" ")) {
                        queryProcessed = query.replace(" ", "%20")
                    }
                    else {
                        queryProcessed = "${query}%20"
                    }
                    Log.d("query", queryProcessed)
                    val urlCity = "https://secure.geonames.org/searchJSON?q=${queryProcessed}&maxRows=15&username=maxrave"
                    val request: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, urlCity, null,
                            { response ->
                                Log.d("response", response.toString())
                                try {
                                    val geonames = response.getJSONArray("geonames")
                                    for (i in 0 until geonames.length()) {
                                        val geoname = geonames.getJSONObject(i)
                                        val adminName1 = geoname.getString("adminName1")
                                        val countryCode = geoname.getString("countryCode")
                                        val countryName = geoname.getString("countryName")
                                        val lat = geoname.getString("lat")
                                        val lng = geoname.getString("lng")
                                        val name = geoname.getString("name")
                                        val population = geoname.getInt("population")
                                        val toponymName = geoname.getString("toponymName")
                                        val geonameX: GeonameX = GeonameX(adminName1,
                                            countryCode,
                                            countryName,
                                            lat,
                                            lng,
                                            name,
                                            population,
                                            toponymName)
                                        listCity.add(geonameX)
                                    }
                                    val recyclerView: RecyclerView = findViewById(R.id.list_cities)
                                    recyclerView.layoutManager = LinearLayoutManager(this@SearchCityActivity)
                                    val adapter: GeonameAdapter = GeonameAdapter(listCity)
                                    recyclerView.adapter = adapter
                                    adapter.onItemClick = {
                                        val intent = Intent(this@SearchCityActivity, MainActivity::class.java)
                                        intent.putExtra("city", it.name)
                                        intent.putExtra("country", it.countryCode)
                                        intent.putExtra("lat", it.lat)
                                        intent.putExtra("lng", it.lng)
                                        startActivity(intent)
                                    }

//                                    adapter.onItemClickListener(object : GeonameAdapter.onItemClickListener {
//                                        override fun onItemClick(position: Int) {
//                                            Toast.makeText(this@SearchCityActivity,
//                                                "You clicked on ${listCity[position].name}",
//                                                Toast.LENGTH_SHORT).show()
//                                            val geonameX = listCity[position]
//                                            val intent:Intent = Intent(this@SearchCityActivity, MainActivity::class.java)
//                                            intent.putExtra("lat", geonameX.lat)
//                                            intent.putExtra("lng", geonameX.lng)
//                                            setResult(RESULT_OK, intent)
//                                            startActivity(intent)
//                                            finish()
//                                        }
//                                    })
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, {
                                error ->
                                Toast.makeText(this@SearchCityActivity,
                                    "Can't access Internet",
                                    Toast.LENGTH_SHORT).show()
                            })
                    requestQueue.add(request)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun mapping(){
        listCityView = findViewById<RecyclerView>(R.id.list_cities)
        searchCityView = findViewById<SearchView>(R.id.search_view)
        listCity = ArrayList()
    }
//    private fun callApi(location: String, listCity: ArrayList<GeonameX>){
//        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
//        val urlCityApi: String = "http://api.geonames.org/searchJSON?q=${location}&maxRows=15&username=maxrave"
//        val stringRequest = StringRequest(com.android.volley.Request.Method.GET, urlCityApi,
//            { response ->
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//                Log.d("Response", response.toString())
//                val jsonObject = JSONObject(response)
//                val geoname = jsonObject.getJSONArray("geonames")
//                for (i in 0 until geoname.length()) {
//                    val geonameObject = geoname.getJSONObject(i)
//                    val geonameX: GeonameX = GeonameX(
//                        geonameObject.getString("adminCode1"),
//                        geonameObject.getString("adminName1"),
//                        geonameObject.getString("countryCode"),
//                        geonameObject.getString("countryId"),
//                        geonameObject.getString("countryName"),
//                        geonameObject.getString("fclName"),
//                        geonameObject.getInt("geonameId"),
//                        geonameObject.getString("lat"),
//                        geonameObject.getString("lng"),
//                        geonameObject.getString("name"),
//                        geonameObject.getInt("population"),
//                        geonameObject.getString("toponymName")
//                    )
//                    listCity.add(geonameX)
//                }
//            },
//            { error -> Log.d("Error.Response", error.toString())})
//        requestQueue.add(stringRequest)
//        Log.d("ListCity", listCity[1].name.toString())
//    }
}