package rajiv.project.com.weatherdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.adapter.DetailAdapter;
import rajiv.project.com.weatherdemo.pojo.List;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.util.ApiClient;
import rajiv.project.com.weatherdemo.util.ApiInterface;
import rajiv.project.com.weatherdemo.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailActivity extends AppCompatActivity {

    private String cityName, dateWeather;
    private WeatherData weatherData;
    private java.util.List<List> dayList = new ArrayList<>();
    private RecyclerView dayRecyclerView;
    private DetailAdapter detailAdapter;
    private Set<String> daySet = new HashSet<>();
    private ApiInterface apiInterface;
    private ImageView nodataImageView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nodataImageView = (ImageView) findViewById(R.id.nodataImageView);
        dayRecyclerView = (RecyclerView) findViewById(R.id.activity_detail_recyclerView);

        initBundle();
        initToolbar();

    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.activity_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Songs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DateFormat df = new SimpleDateFormat("dd");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dd = fmt.parse(dateWeather);
            if (df.format(dd).equals(df.format(new Date()))) {

                getSupportActionBar().setTitle("Today");

            } else {

                Date date = fmt.parse(dateWeather);
                SimpleDateFormat fmtOut = new SimpleDateFormat("EE dd MMM");
                getSupportActionBar().setTitle(fmtOut.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void initBundle() {

        if (getIntent().hasExtra("city")) {
            cityName = getIntent().getStringExtra("city");
            dateWeather = getIntent().getStringExtra("date");
            if (cityName != null) {
                retroFetch(cityName);
            }
        }
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
            }
        });
    }


    private void initRecyclerView() {
        detailAdapter = new DetailAdapter(this, weatherData, dayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerView.setAdapter(detailAdapter);
        detailAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
