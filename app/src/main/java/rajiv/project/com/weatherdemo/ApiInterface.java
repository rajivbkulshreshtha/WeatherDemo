package rajiv.project.com.weatherdemo;

import rajiv.project.com.weatherdemo.pojo.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SUJAN on 11-Oct-17.
 */

public interface ApiInterface {

    @GET("forecast")
    Call<WeatherData> getWeather(@Query("q") String location,
                                 @Query("appid") String appid);


    @GET("weather?appid=e8ca5cd8880c10f1a26c97aa1fa71d3a")
    Call<WeatherData> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon);
}
