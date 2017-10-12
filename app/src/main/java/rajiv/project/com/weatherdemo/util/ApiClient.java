package rajiv.project.com.weatherdemo.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SUJAN on 11-Oct-17.
 */

public class ApiClient {

    private static final String URL = "http://api.openweathermap.org/data/2.5/";

    public static Retrofit retrofit;

    public static Retrofit getApiClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
