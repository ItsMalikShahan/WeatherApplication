package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RelativeLayout home;
    ProgressBar loadingPB;
    TextView cityNameTV, temperatureFigure, temperatureCondition, humidity, wind, feelLike, cloud, sunRise, sunSet;
    RecyclerView weatherDetail;
    TextInputEditText cityEdit;
    ImageView weatherIcon, search, backScreen;
    ArrayList<WeatherDetailModel> weatherArraylist;
    RecyclerView weather;
    WeatherDetailAdapter weatherDetailAdapter;
    LocationManager locationManager;
    int PERMISSION_CODE = 1;
    String searchCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is used to make our window full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        home = findViewById(R.id.rl_home);
        loadingPB = findViewById(R.id.pb_loading);
        cityNameTV = findViewById(R.id.tv_cityName);
        temperatureFigure = findViewById(R.id.tv_tempFigure);
        temperatureCondition = findViewById(R.id.tv_tempCond);
        weatherDetail = findViewById(R.id.rv_weatherDetail);
        cityEdit = findViewById(R.id.tie_city);
        weatherIcon = findViewById(R.id.iv_tempIcon);
        weather = findViewById(R.id.rv_weatherDetail);
        search = findViewById(R.id.iv_search);
        backScreen = findViewById(R.id.iv_backScreen);
        humidity = findViewById(R.id.tv_humidityValue);
        wind = findViewById(R.id.tv_windValue);
        feelLike = findViewById(R.id.tv_feelLikeValue);
        cloud = findViewById(R.id.tv_cloudValue);
        sunRise = findViewById(R.id.tv_sunriseValue);
        sunSet = findViewById(R.id.tv_sunsetValue);


        weatherArraylist = new ArrayList<>();
        weatherDetailAdapter = new WeatherDetailAdapter(this, weatherArraylist);
        weather.setAdapter(weatherDetailAdapter);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            searchCityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(searchCityName);
        } else
            searchCityName = "Islamabad";
        getWeatherInfo(searchCityName);
        Log.e("TAG", "onCreate: Set default location Islamabad");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please provide city name", Toast.LENGTH_SHORT).show();
                } else
                    cityNameTV.setText(city);
                getWeatherInfo(city);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please Provide permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public String getCityName(double longitude, double latitude) {
        String cityName = "Islamabad";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(longitude, latitude, 10);

            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;

                    } else {
                        Log.d("TAG", " City not found ");
                        Toast.makeText(this, "User City not found", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }


    public void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=c5144ba145fb45049de04259232507&q=" + cityName + "&days=1&aqi=no&alerts=no";
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//        Log.e("TAG", "getWeatherInfo: check 1" );
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                weatherArraylist.clear();
//                Log.e("TAG", "getWeatherInfo: check 2" );

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureFigure.setText(temperature + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String cloudStr = response.getJSONObject("current").getString("cloud");
                    String humidityStr = response.getJSONObject("current").getString("humidity");
                    String feelLikeStr = response.getJSONObject("current").getString("feelslike_c");
                    String windStr = response.getJSONObject("current").getString("wind_kph");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weatherIcon);
                    temperatureCondition.setText(condition);
                    cloud.setText(cloudStr);
                    humidity.setText(humidityStr);
                    feelLike.setText(feelLikeStr+"°C");
                    wind.setText(windStr+"km/h");

                    if (isDay == 1) {
                        // Morning
                        Picasso.get().load("http://unsplash.com/photos/OHzkfrv9Ycw").into(backScreen);
                    } else {

                        Picasso.get().load("http://unsplash.com/photos/bWtd1ZyEy6w").into(backScreen);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    String sunRiseStr = forecastO.getJSONObject("astro").getString("sunrise");
                    String sunSetStr = forecastO.getJSONObject("astro").getString("sunset");
//                    String sunRiseStr = forecastObj.getJSONObject("astro").getString("sunrise");
//                    Log.e("TAG", "onResponse: "+sunRiseStr );
                    sunRise.setText(sunRiseStr);
                    sunSet.setText(sunSetStr);

                    for (int i = 0; i < hourArray.length(); i++) {
//                        Log.e("TAG", "getWeatherInfo: check 4" );

                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherArraylist.add(new WeatherDetailModel(time, temp, img, wind));
                    }
                    weatherDetailAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please provide valid city name..", Toast.LENGTH_SHORT).show();
//                Log.e("TAG", "Volley Error: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}