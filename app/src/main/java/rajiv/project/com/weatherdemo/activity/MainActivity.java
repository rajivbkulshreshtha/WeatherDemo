package rajiv.project.com.weatherdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.com.mauker.materialsearchview.MaterialSearchView;
import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.adapter.MainAdapter;
import rajiv.project.com.weatherdemo.pojo.WeatherData;
import rajiv.project.com.weatherdemo.util.ApiClient;
import rajiv.project.com.weatherdemo.util.ApiInterface;
import rajiv.project.com.weatherdemo.util.Constants;
import rajiv.project.com.weatherdemo.util.DataLoader;
import rajiv.project.com.weatherdemo.util.LocalDbHelper;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private Location lastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private double currentLatitude;
    private double currentLongitude;
    private String currentDate, currentTime;

    private ApiInterface apiInterface;

    private WeatherData weatherData;

    private List<rajiv.project.com.weatherdemo.pojo.List> dayList = new ArrayList<>();
    private Set<String> daySet = new HashSet<>();

    private LocalDbHelper localDbHelper;

    private RecyclerView dayRecyclerView;
    private MainAdapter mainAdapter;

    private MaterialSearchView searchView;
    private View currentView;
    private Toolbar toolbar;
    private ImageView noDataImageView, currentClimateImage;
    private TextView currentLocationTextView, currentDateTextView, currentClimateNameTextView,
            currentTempTextView, currentHighestTempTextView, currentLowestTempTextView, currentWindSpeedTextView, currentHumidTextView;

    private boolean course_location, fine_location, external_file;

    private LoaderManager.LoaderCallbacks<String> loaderCallbacks = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new DataLoader(MainActivity.this, localDbHelper, searchView);
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {

        }


        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };

    private void initSharedPreference() {

        SharedPreferences userSharedPreferences = getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        course_location = userSharedPreferences.getBoolean(Constants.PER_COURSE_LOCATION, false);
        fine_location = userSharedPreferences.getBoolean(Constants.PER_FINE_LOCATION, false);
        external_file = userSharedPreferences.getBoolean(Constants.PER_EXTERNAL_FILE, false);

        if (!course_location && !fine_location) {
            ((LinearLayout) currentView.findViewById(R.id.layout_current_climate_no_currentData_layout)).setVisibility(View.VISIBLE);

        }

    }

    private void initCurrentView() {
        currentView = findViewById(R.id.include_current_climate);

        currentClimateImage = (ImageView) currentView.findViewById(R.id.layout_current_climate_image);

        currentLocationTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_location);
        currentDateTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_date);
        currentClimateNameTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_type);
        currentTempTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_current_temp);
        currentHighestTempTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_highest_temp);
        currentLowestTempTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_lowest_temp);
        currentWindSpeedTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_wind_speed);
        currentHumidTextView = (TextView) currentView.findViewById(R.id.layout_current_climate_humid);

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noDataImageView = (ImageView) findViewById(R.id.nodataImageView);
        dayRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);

        initSearchView();
        initCurrentView();
    }

    private void initRecyclerView() {
        mainAdapter = new MainAdapter(this, weatherData, dayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerView.setAdapter(mainAdapter);
        mainAdapter.notifyDataSetChanged();
    }

    private void initSearchView() {

        searchView = (MaterialSearchView) findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    Toast.makeText(MainActivity.this, "Please add City name", Toast.LENGTH_SHORT).show();
                    return true;

                } else {
                    retroFetch(query);
                    return false;

                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestion = searchView.getSuggestionAtPosition(position);
                if (suggestion.equals("")) {
                    Toast.makeText(MainActivity.this, "Please add City name", Toast.LENGTH_SHORT).show();
                    searchView.setQuery(suggestion, false);
                } else {
                    retroFetch(suggestion);
                    searchView.setQuery(suggestion, true);
                }
                searchView.setQuery(suggestion, false);
            }
        });


        searchView.adjustTintAlpha(0.8f);


        searchView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, "Long clicked position: " + i, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        /*searchView.setOnVoiceClickedListener(new MaterialSearchView.OnVoiceClickedListener() {
            @Override
            public void onVoiceClicked() {
                //Toast.makeText(MainActivity.this, "Voice clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            localDbHelper = new LocalDbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        init();


        mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        initSharedPreference();
    }


    private void retroFetch(String location) {
        ((LinearLayout) currentView.findViewById(R.id.layout_current_climate_no_currentData_layout)).setVisibility(View.GONE);
        daySet.clear();
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
                                String[] DateArray = day.getDtTxt().split(" ");
                                String dayDate = DateArray[0].trim();
                                String dayTime = DateArray[1].trim();

                                String currentDateTimeString = new SimpleDateFormat("dd").format(new Date());

                                if (currentDateTimeString.equals(date)) {

                                    currentLocationTextView.setText(weatherData.getCity().getName() + " , " + weatherData.getCity().getCountry());

                                    String[] currentDateArray = day.getDtTxt().split(" ");
                                    currentDate = currentDateArray[0].trim();
                                    currentTime = currentDateArray[1].trim();

                                    currentDateTextView.setText("Today");

                                    currentClimateNameTextView.setText(day.getWeather().get(0).getDescription().toUpperCase());

                                    double currentTemp = Constants.getTemp(MainActivity.this, day.getMain().getTemp());
                                    double highestTemp = Constants.getTemp(MainActivity.this, day.getMain().getTempMax());
                                    double lowestTemp = Constants.getTemp(MainActivity.this, day.getMain().getTempMin());


                                    currentTempTextView.setText(String.format("%.0f˚ ", currentTemp));
                                    currentHighestTempTextView.setText(String.format("%.0f˚", highestTemp));
                                    currentLowestTempTextView.setText(String.format("%.0f˚", lowestTemp));

                                    currentWindSpeedTextView.setText(String.format("%.2f km/h", day.getWind().getSpeed()));
                                    currentHumidTextView.setText(String.format("%d %%", day.getMain().getHumidity()));

                                    currentClimateImage.setImageResource(Constants.getImage(day.getWeather().get(0).getIcon()));

                                    daySet.add(date);

                                    currentView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(MainActivity.this, DetailActivity.class)
                                                    .putExtra("city", weatherData.getCity().getName()).putExtra("date", currentDate));
                                        }
                                    });

                                } else {
                                    if (currentTime != null && currentTime.equals(dayTime)) {
                                        dayList.add(day);
                                        daySet.add(date);

                                    }
                                }

                            }

                            if (dayList != null) {
                                initRecyclerView();
                                if (mainAdapter != null) {
                                    mainAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        dayRecyclerView.setVisibility(View.GONE);
                        noDataImageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                searchView.openSearch();
                return true;
            case R.id.action_my_location:
                permissionChecks();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void permissionChecks() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_LOCATION);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_LOCATION);

            }
        } else {
            setLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0) {

                    boolean course_location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean fine_location = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean external_file = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    SharedPreferences.Editor userPreferenceEditor = getSharedPreferences(Constants.USER_PREF, Context.MODE_PRIVATE).edit();

                    if (course_location) {
                        userPreferenceEditor.putBoolean(Constants.PER_COURSE_LOCATION, true);
                    }

                    if (fine_location) {
                        userPreferenceEditor.putBoolean(Constants.PER_FINE_LOCATION, true);
                    }


                    if (external_file) {
                        userPreferenceEditor.putBoolean(Constants.PER_EXTERNAL_FILE, true);
                    }

                    userPreferenceEditor.apply();
                    userPreferenceEditor.commit();

                    if (course_location && fine_location) {

                        setLocation();

                        if (external_file) {
                            Constants.isGranted = true;
                        }

                    } else {
                    }
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void setLocation() {


        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation == null) {
            lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (lastLocation != null) {

            currentLatitude = lastLocation.getLatitude();
            currentLongitude = lastLocation.getLongitude();

            try {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                String cityName = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getCountryName();
                if (cityName != null && countryName != null) {
                    retroFetch(cityName + "," + countryName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ((LinearLayout) currentView.findViewById(R.id.layout_current_climate_no_currentData_layout)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        searchView.activityResumed();

        if (external_file) {
            if (localDbHelper != null) {
                getSupportLoaderManager().initLoader(Constants.LOADER_CODE, null, loaderCallbacks);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (course_location && fine_location) {
            setLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}

