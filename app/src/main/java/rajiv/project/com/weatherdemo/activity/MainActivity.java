package rajiv.project.com.weatherdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.adapter.MainAdapter;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.singleton.VolleySingleton;
import rajiv.project.com.weatherdemo.util.ApiClient;
import rajiv.project.com.weatherdemo.util.ApiInterface;
import rajiv.project.com.weatherdemo.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_LOCATION = 202;
    Location lastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private RecyclerView dayRecyclerView;
    private MainAdapter mainAdapter;
    private WeatherData weatherData;
    private EditText searchEditText;
    private ImageView nodataImageView, searchImageView;
    private ApiInterface apiInterface;


    private List<rajiv.project.com.weatherdemo.pojo.List> dayList = new ArrayList<>();
    private Set<String> daySet = new HashSet<>();


    private String CityName, Country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nodataImageView = (ImageView) findViewById(R.id.nodataImageView);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        dayRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        permissionChecks();

        //fetchWeatherByName("Mumbai,IN");

        doSearch();
    }

    private void doSearch() {

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please add City name", Toast.LENGTH_SHORT).show();
                } else {

                    retroFetch(searchEditText.getText().toString());
                }
            }
        });

    }

    private void fetchWeatherByName(String location) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL + "/forecast?q=" + location + "&appid=" + Constants.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        weatherData = new Gson().fromJson(response, WeatherData.class);
                        if (weatherData != null) {

                            for (rajiv.project.com.weatherdemo.pojo.List day : weatherData.getList()) {

                                java.util.Date time = new java.util.Date((long) day.getDt());
                                String[] text = time.toString().split(" ");
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
                    }
                }) {

        };
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
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
                            String date = dateArray[0].substring(8, 10);

                            if (!daySet.contains(date)) {
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


    private void fetchCurrentWeather(String latitude, String longitude) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL + "/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + Constants.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        weatherData = new Gson().fromJson(response, WeatherData.class);

                        if (weatherData != null) {
                            //initRecyclerView();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

        };
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void permissionChecks() {
        Log.d("Connected", "Check Permissions");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(MainActivity.this, "Need to access Location", Toast.LENGTH_LONG).show();
                }
                Log.d("Connected", "REQUEST PERMISSIONS");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            }
        } else {
            try{

                lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                currentLatitude = lastLocation.getLatitude();
                currentLongitude = lastLocation.getLongitude();

            }catch (Exception e){

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //mContactList = ContactFetcher.getInstance(this).fetchAll();
                    //initContactRecyclerView();

                    setLocation();


                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }




       /* if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("asd", "onRequestPermissionsResult: "+permissions[0]);
                setLocation();

            } else {
                Toast.makeText(MainActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSION_READ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mContactList = ContactFetcher.getInstance(this).fetchAll();
                    initContactRecyclerView();

                } else {

                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Permission required", Snackbar.LENGTH_LONG)
                            .setAction("Try Again", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constant.PERMISSION_READ_CODE);
                                }
                            });

                    snackbar.show();
                }
                return;
            }
        }
    }*/


    private void setLocation() {


        permissionChecks();
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            currentLatitude = lastLocation.getLatitude();
            currentLongitude = lastLocation.getLongitude();
            Log.d("Latitude", String.valueOf(currentLatitude));
            Log.d("Longitude", String.valueOf(currentLongitude));
            try {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

                if (cityName != null && countryName != null) {
                    retroFetch(cityName + "," + countryName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        setLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    private void initRecyclerView() {

        mainAdapter = new MainAdapter(this, weatherData, dayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerView.setAdapter(mainAdapter);
        mainAdapter.notifyDataSetChanged();

    }

}
