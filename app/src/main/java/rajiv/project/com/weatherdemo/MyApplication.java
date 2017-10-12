package rajiv.project.com.weatherdemo;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by SUJAN on 22-Aug-17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        defaultFont();
    }

    private void defaultFont() {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
