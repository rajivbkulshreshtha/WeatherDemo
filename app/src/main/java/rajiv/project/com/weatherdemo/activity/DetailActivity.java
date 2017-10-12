package rajiv.project.com.weatherdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import rajiv.project.com.weatherdemo.util.ApiClient;
import rajiv.project.com.weatherdemo.util.ApiInterface;
import rajiv.project.com.weatherdemo.util.Constants;
import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.adapter.DetailAdapter;
import rajiv.project.com.weatherdemo.pojo.List;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.singleton.VolleySingleton;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailActivity extends AppCompatActivity {

    String cityName, dateWeather;
    private WeatherData weatherData;
    private java.util.List<List> dayList = new ArrayList<>();
    private RecyclerView dayRecyclerView;
    private DetailAdapter detailAdapter;
    private Set<String> daySet = new HashSet<>();
    private ApiInterface apiInterface;
    private ImageView nodataImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        nodataImageView = (ImageView) findViewById(R.id.nodataImageView);
        dayRecyclerView = (RecyclerView) findViewById(R.id.activity_detail_recyclerView);


        initBundle();

    }

    private void initBundle() {

        if (getIntent().hasExtra("city")) {

            cityName = getIntent().getStringExtra("city");
            dateWeather = getIntent().getStringExtra("date");

            if (cityName != null) {
                Log.d("AAQQ", "cityName: " + cityName);

                retroFetch(cityName);
            }
        }

       /* Log.d("AAQQ", "cityName: " + cityName);
        retroFetch(cityName);*/

    }


    private void retroFetch(String location) {

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<WeatherData> call = apiInterface.getWeather(location, Constants.API_KEY);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, retrofit2.Response<WeatherData> response) {

                if (response.isSuccessful()) {

                    weatherData = response.body();

                    if (weatherData != null) {

                        for (rajiv.project.com.weatherdemo.pojo.List day : weatherData.getList()) {


                            String[] dateArray = day.getDtTxt().split(" ");
                            String date = dateArray[0];

                            Log.d("AAQQ", "date: " + date);
                            Log.d("PPOI", "dateArray[0]: " + dateArray[0]);

                            if (dateWeather.contains(date)) {
                                daySet.add(date);
                                dayList.add(day);
                            }

                            if (dayList != null) {
                                initRecyclerView();

                            }
                        }
                    } else {
                        dayRecyclerView.setVisibility(View.GONE);
                        nodataImageView.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d("QQQQQ", "Some error: " + t.getMessage());
            }
        });

    }


    private void fetchWeatherByName(String location) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL + "/forecast?q=" + location + "&appid=" + Constants.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        weatherData = new Gson().fromJson(response, WeatherData.class);
                        Log.d("ABC", weatherData.getCod());

                        if (weatherData != null) {

                            for (rajiv.project.com.weatherdemo.pojo.List day : weatherData.getList()) {

                                java.util.Date time = new java.util.Date((long) day.getDt());
                                String[] text = time.toString().split(" ");
                                Log.d("QQd", text[2]);
                                if (!daySet.contains(text[2])) {
                                    daySet.add(text[2]);
                                    dayList.add(day);
                                }

                                if (dayList != null) {
                                    initRecyclerView();

                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GRES", error.toString());
                    }
                }) {

        };
        VolleySingleton.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);
    }


    private void initRecyclerView() {

        detailAdapter = new DetailAdapter(this, weatherData, dayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerView.setAdapter(detailAdapter);
        detailAdapter.notifyDataSetChanged();

    }

}
