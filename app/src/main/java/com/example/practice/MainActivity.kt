package com.example.practice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

//cb1568b47e446f4a1eba89f4fead0b1e

class MainActivity : AppCompatActivity() {
    private  val  binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Roorkee")
        searchCity()

    }

    private fun searchCity() {
        val searchView=binding.searchBar
        searchView.queryHint = "Search For A City"
        val searchText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchText.setHintTextColor(ContextCompat.getColor(this, R.color.black))
        searchText.setTextColor(ContextCompat.getColor(this, R.color.black))
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
         override fun onQueryTextSubmit(query: String?): Boolean {
             if (query != null) {
                 fetchWeatherData(query)
                 searchView.clearFocus()
             }
             return true
         }

         override fun onQueryTextChange(newText: String?): Boolean {
             return true
         }

     })
        searchView.isIconified = false
        searchView.clearFocus()

    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"cb1568b47e446f4a1eba89f4fead0b1e","metric")
        response.enqueue(object:Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody !=null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unKnown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min
                    binding.tempreature.text="$temperature"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max_Temp $maxTemp C"
                    binding.minTemp.text="Min_Temp $minTemp C"
                    binding.humidity1.text="$humidity %"
                    binding.windSpeed1.text="$windspeed m/s"
                    binding.sunRise.text="${time(sunRise)}"
                    binding.sunSet.text="${time(sunSet)}"
                    binding.sea1.text="$seaLevel hPa"
                    binding.condition1.text=condition
                  //  binding.
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"

                    changeImageAccordingWeatherCondition(condition)



                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            }

        })
    }

    private fun changeImageAccordingWeatherCondition(condition :String) {
        when(condition){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_screen)
                binding.weatherImage.setAnimation(R.raw.sunny)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.weatherImage.setAnimation(R.raw.cloudy)

            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavey Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain)
                binding.weatherImage.setAnimation(R.raw.rain1)


            }
            "Light Snow","Moderate Snow","Heavy Rain","Bizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.weatherImage.setAnimation(R.raw.snow1)


            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_screen)
                binding.weatherImage.setAnimation(R.raw.sunny)


            }
        }
        binding.weatherImage.playAnimation()

    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp:Long): String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}