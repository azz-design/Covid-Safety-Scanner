package com.example.covid1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(haveNetwork()) {

        }else if(!haveNetwork()){
            Toast.makeText(this, "Network Connection is not available!!", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(HomeActivity.this,LoginFragment.class);
                startActivity(homeIntent);
                finish();

            }
        },SPLASH_TIME_OUT);







    }
    private Boolean haveNetwork(){

        boolean have_WIFI=false;
        boolean have_MobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkinfo = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkinfo){
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected()) {
                    have_WIFI = true;
                }
            if(info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected()) {
                    have_MobileData = true;
                }

        }
        return have_MobileData||have_WIFI;

    }


}
