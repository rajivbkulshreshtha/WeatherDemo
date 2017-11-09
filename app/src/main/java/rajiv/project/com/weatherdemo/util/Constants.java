package rajiv.project.com.weatherdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import rajiv.project.com.weatherdemo.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SUJAN on 12-Oct-17.
 */

public class Constants {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "e8ca5cd8880c10f1a26c97aa1fa71d3a";


    public static final int REQUEST_CODE_LOCATION = 202;
    public static final int SPLASH_DISPLAY_LENGTH = 3000;
    public static final int REQUEST_LOCATION = 202;
    public static final String USER_PREF = "9623";
    public static final String USER_PREF_TEMP_UNIT_FER = "879452";
    public static final String PER_COURSE_LOCATION = "5462";
    public static final String PER_FINE_LOCATION = "3123123";
    public static final String PER_EXTERNAL_FILE = "78515648";
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int LOADER_CODE = 154;
    public static boolean isGranted = false;

    public static int getImage(String imageStr) {

        switch (imageStr) {
            case "01d":
                return R.drawable._36;
            case "02d":
                return R.drawable._28;
            case "03d":
                return R.drawable._49;
            case "04d":
                return R.drawable._26;
            case "04n":
                return R.drawable._26;
            case "10d":
                return R.drawable._39;
            case "11d":
                return R.drawable._04;
            case "13d":
                return R.drawable._14;
            case "50d":
                return R.drawable._23;
            case "50n":
                return R.drawable._23;
            case "01n":
                return R.drawable._31;
            case "02n":
                return R.drawable._27;
            case "03n":
                return R.drawable._49;
            case "10n":
                return R.drawable._45;
            case "11n":
                return R.drawable._04;
            case "13n":
                return R.drawable._14;
            default:
                return 0;
        }

    }

    public static double getTemp(Context context, double temp) {

        SharedPreferences userSharedPreferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        boolean tempUnit = userSharedPreferences.getBoolean(Constants.USER_PREF_TEMP_UNIT_FER, false);

        if (tempUnit) {
            return temp * (9.0 / 5) - 459.67;
        } else {
            return temp - 273.15;
        }

    }
}
