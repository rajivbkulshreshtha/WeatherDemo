package rajiv.project.com.weatherdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rajiv.project.com.weatherdemo.R;
import rajiv.project.com.weatherdemo.util.Constants;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //check for permission
        //if all granted load splashHandler
        //else request for the permission
        permissionChecks();

        //if all the permission is granted then load handler
        if (Constants.isGranted) {
            splashHandler();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE_LOCATION) {
            //if any permission is granted then grantResult will be greater then 0
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


                if (course_location && fine_location && external_file) {
                    //if all the permission are accepted then goto mainActivity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    Constants.isGranted = true;
                } else {
                    //else show snackbar for ask again functionality
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.splashContainer), "Need permission for your location", Snackbar.LENGTH_LONG).setAction("Ask Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionChecks();
                        }
                    });
                    snackbar.show();

                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);

                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();

                        }
                    });

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void permissionChecks() {
        //check if all permission is granted
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

            Constants.isGranted = true;

        }
    }

    //after SPLASH_DISPLAY_LENGTH ie. 3 second
    //will goto MainActivity
    private void splashHandler() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();

            }

        }, Constants.SPLASH_DISPLAY_LENGTH);

    }
}
