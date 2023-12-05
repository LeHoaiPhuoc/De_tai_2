package example.de_tai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RelativeLayout RLHome;
    ProgressBar PBLoading;
    TextView TVCityName, TVTemperature1, TVCondition;
    TextInputEditText EdtCity;
    ImageView IVBack, IVIcon, IVSearch;
    RecyclerView RVWeather;
    ArrayList<WeatherRVModel> weatherRVModelArrayList;
    WeatherRVAdaper weatherRVAdaper;
    LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addControl();
        addEvent();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            cityName = getCityName(longitude, latitude);
            getWeatherInfo(cityName);
        } else {
            // Handle the case where location is null or unavailable
            // For instance, you might request location updates or display an error message
            // You could also set a default location or prompt the user to enable location services
            Toast.makeText(this, "Location information unavailable", Toast.LENGTH_SHORT).show();
        }

 //hiii
        //Set background
//        RelativeLayout relativeLayout = findViewById(R.id.main_layout);
//        Calendar calendar = Calendar.getInstance();
//        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//
//        if(currentHour >= 5 && currentHour <= 11) {
//            AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.gradient_list_morning);
//            relativeLayout.setBackground(animationDrawable);
//            animationDrawable.setEnterFadeDuration(2500);
//            animationDrawable.setExitFadeDuration(5000);
//            animationDrawable.start();
//        } else if (currentHour >= 12 && currentHour <= 16) {
//            AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.gradient_list_afternoon);
//            relativeLayout.setBackground(animationDrawable);
//            animationDrawable.setEnterFadeDuration(2500);
//            animationDrawable.setExitFadeDuration(5000);
//            animationDrawable.start();
//        } else if(currentHour >= 17 && currentHour <= 19) {
//            AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.gradient_list_evening);
//            relativeLayout.setBackground(animationDrawable);
//            animationDrawable.setEnterFadeDuration(2500);
//            animationDrawable.setExitFadeDuration(5000);
//            animationDrawable.start();
//        } else if (currentHour >= 20 && currentHour <= 4) {
//            AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.gradient_list_night);
//            relativeLayout.setBackground(animationDrawable);
//            animationDrawable.setEnterFadeDuration(2500);
//            animationDrawable.setExitFadeDuration(5000);
//            animationDrawable.start();
//        }

    }
    public void addControl(){
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        RLHome = (RelativeLayout) findViewById(R.id.RLHome);
        PBLoading = (ProgressBar) findViewById(R.id.PBLoading);
        TVCityName = (TextView) findViewById(R.id.TVCityName);
        TVTemperature1 = (TextView) findViewById(R.id.TVTemperature1);
        TVCondition = (TextView) findViewById(R.id.TVCondition);
        EdtCity = (TextInputEditText) findViewById(R.id.EdtCity);
        IVIcon = (ImageView) findViewById(R.id.IVIcon);
        IVSearch = (ImageView) findViewById(R.id.IVSearch);
        IVBack = (ImageView) findViewById(R.id.IVBack);
        RVWeather = (RecyclerView) findViewById(R.id.RVWeather);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdaper = new WeatherRVAdaper(this,weatherRVModelArrayList);
        RVWeather.setAdapter(weatherRVAdaper);
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    public void addEvent(){
        IVSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = EdtCity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city name", Toast.LENGTH_SHORT).show();

                }else {
                    TVCityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void getWeatherInfo(String cityName){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=0f4ce91ee1a24deebce53135232211&q="+cityName+"&days=3&aqi=no&alerts=no";
        TVCityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                PBLoading.setVisibility(View.GONE);
//                RLHome.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    TVTemperature1.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(IVIcon);
                    TVCondition.setText(condition);


                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for(int i = 0; i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time, temper,img, wind));
                        weatherRVAdaper.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public String getCityName(double longtitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude, longtitude,10);
            for (Address adr : addresses){
                if (adr != null){
                    String city = adr.getLocality();
                    if(city !=null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this,"User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }


}