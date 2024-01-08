package com.example.jhenskie;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    TextView  text, loc11, location1;
    GifImageView sun1, sunrain1, cloudy1, cloudynight1, thunderstorm1, cloudyday1, nightrain1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 101;
    private FusedLocationProviderClient fusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thunderstorm1 = findViewById(R.id.thunderstorm);
        cloudyday1 = findViewById(R.id.cloudyday);
        cloudy1 = findViewById(R.id.cloudy);
        cloudynight1 = findViewById(R.id.cloudynight);
        sunrain1 = findViewById(R.id.sunrain);
        nightrain1 = findViewById(R.id.nightrain);
        text = findViewById(R.id.loc);
        loc11 = findViewById(R.id.loc1);
        location1 = findViewById(R.id.location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Call method to fetch weather using latitude and longitude
                            fetchWeather(latitude, longitude);

                            // Get address based on latitude and longitude
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (!addresses.isEmpty()) {
                                    String city = addresses.get(0).getLocality();
                                    String country = addresses.get(0).getCountryName();

                                    String locationText = "" + city + "\n" + country;
                                    location1.setText(locationText);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void fetchWeather(double latitude, double longitude) {
        String apiKey = "201a67abeec47d8ef8db5a1b8be8f4cc";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Failed to fetch weather data. Check your internet connection.",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    String responseData = response.body().string();
                    WeatherData weatherData = new Gson().fromJson(responseData, WeatherData.class);

                    if (weatherData != null) {
                        double temperatureCelsius = weatherData.getMain().getTemp();
                        String weatherCondition = "";

                        if (weatherData.getWeather() != null && weatherData.getWeather().length > 0) {
                            weatherCondition = weatherData.getWeather()[0].getDescription();
                        }

                        final String finalWeatherCondition = weatherCondition;
                        runOnUiThread(() -> {
                            // Display temperature and weather condition in the TextView
                            Calendar calendar = Calendar.getInstance();
                            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                            String temperatureText = String.format("\t%.1f°C\n", temperatureCelsius);
                            String weatherConditionText = String.format("\t%s\n", finalWeatherCondition);
                            text.setText(temperatureText);
                            loc11.setText(weatherConditionText);
                            if (finalWeatherCondition.equals("clear sky") || finalWeatherCondition.equals("few clouds") || finalWeatherCondition.equals("broken clouds") || finalWeatherCondition.equals("scattered clouds")) {
                                if (hourOfDay >= 0 && hourOfDay < 4) {
                                    cloudynight1.setVisibility(View.VISIBLE);
                                } else if (hourOfDay >= 6 && hourOfDay < 18) {
                                    cloudyday1.setVisibility(View.VISIBLE);
                                } else {
                                    cloudynight1.setVisibility(View.VISIBLE);
                                }
                            }
                            if (finalWeatherCondition.equals("overcast clouds")) {

                                cloudy1.setVisibility(View.VISIBLE);

                            }
                            if (finalWeatherCondition.equals("thunderstorm with light rain") || finalWeatherCondition.equals("thunderstorm with rain") || finalWeatherCondition.equals("thunderstorm with heavy rain") ||
                                    finalWeatherCondition.equals("light thunderstorm") || finalWeatherCondition.equals("thunderstorm with rain") || finalWeatherCondition.equals("thunderstorm") || finalWeatherCondition.equals("heavy thunderstorm")
                                    || finalWeatherCondition.equals("ragged thunderstorm") || finalWeatherCondition.equals("thunderstorm with light drizzle") || finalWeatherCondition.equals("thunderstorm with drizzle") || finalWeatherCondition.equals("thunderstorm with heavy drizzle")) {
                                thunderstorm1.setVisibility(View.VISIBLE);
                            }
                            if (finalWeatherCondition.equals("light rain") || finalWeatherCondition.equals("moderate rain") || finalWeatherCondition.equals("heavy intensity rain") ||
                                    finalWeatherCondition.equals("very heavy rain") || finalWeatherCondition.equals("extreme rain") || finalWeatherCondition.equals("Freezing rain") || finalWeatherCondition.equals("light intensity shower rain")
                                    || finalWeatherCondition.equals("shower rain") || finalWeatherCondition.equals("heavy intensity shower rain") || finalWeatherCondition.equals("ragged shower rain")) {
                                if (hourOfDay >= 0 && hourOfDay < 4) {
                                    nightrain1.setVisibility(View.VISIBLE);
                                } else if (hourOfDay >= 6 && hourOfDay < 18) {
                                    sunrain1.setVisibility(View.VISIBLE);
                                } else {
                                    nightrain1.setVisibility(View.VISIBLE);
                                }

                            }

                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,
                                "Failed to get weather data. Error code: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
