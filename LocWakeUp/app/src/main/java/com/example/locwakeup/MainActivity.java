package com.example.locwakeup;

import static android.util.Log.d;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textDesLat, textDesLong, textCurLat, textCurLong, textDist;
    private Switch aSwitch;

    private Button  button;

    private BroadcastReceiver broadcastReceiver;

    //Static Codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //Internal Variables

    private long locUpdateDelay=1000;  //in milli seconds
    private float locMinDistance=1;  //in meters

    private float alarmDistance = 1000; //in meters

    private long vibrationTime = 20000;

    private double destinationLat = 12.926235;
    private double destinationLong= 77.683202;

    private float distance ;
    private int switchStatus;

    {
        switchStatus = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get UI Objects
        textCurLat=findViewById(R.id.textCurLat);
        textCurLong=findViewById(R.id.textCurLong);
        textDesLat=findViewById(R.id.textDesLat);
        textDesLong=findViewById(R.id.textDesLong);

        textDist=findViewById(R.id.textDist);
        aSwitch=findViewById(R.id.locEnable);

        button=findViewById(R.id.buttonAdd);

        //Initialize Location Manager, Vibrator & Media player
        //locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);


        //Update Destination Lat and Long
        textDesLat.setText("Lat " + destinationLat);
        textDesLong.setText("Long " + destinationLong );




        //Initialize Slide Switch and Call back
        initializeSlideSwitch();

        initializeButton();

        initializeBroadcast();


        //Check and Get Permission for Location Service

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }



    }

    //Register Switch Callback

    private void initializeSlideSwitch()
    {
        Log.d(TAG, "initializeSlideSwitch: ");
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                switchStatus=1;
                Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
                intent.putExtra("destinationLat", destinationLat);
                intent.putExtra("destinationLong", destinationLong);
                intent.putExtra("vibrationTime", vibrationTime);
                intent.putExtra("alarmDistance", alarmDistance);
                intent.putExtra("locUpdateDelay", locUpdateDelay);
                intent.putExtra("locMinDistance", locMinDistance);

                startService(intent);
            }
            else{
                switchStatus=0;
                Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
                stopService(intent);
            }
        });
    }

    private void initializeButton()
    {
        Log.d(TAG, "initializeButton: ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, subActivity.class);
                startActivity(intent);
            }
        });
    }




   private void updateLocation(Location location, float distance)
   {
       Log.d(TAG, "updateLocation: ");
       double latitude = location.getLatitude();
       double longitude = location.getLongitude();

       //Update Current Lat and Long
       textCurLat.setText("Lat " + latitude);
       textCurLong.setText("Long " + longitude);

       //Update Distance
       textDist.setText("Dist " + distance);
   }

   private void initializeBroadcast()
   {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location location = intent.getParcelableExtra("location");
                distance = intent.getFloatExtra("distance", 0);

                Log.d(TAG, "Broadcast Received: Location " + location.getLatitude() + " " + location.getLongitude());
                Log.d(TAG, "Broadcast Received: Distance " + distance);

                if (location != null) {
                    updateLocation(location, distance);
                }


            }
        };


        IntentFilter intentFilter = new IntentFilter("locWakeUp_location_update");
        registerReceiver(broadcastReceiver, intentFilter);

   }





 protected void onDestroy()
 {
     Log.d(TAG, "onDestroy: ");
        super.onDestroy();

//
//        if (locationManager != null) {
//            locationManager.removeUpdates(locationListener);
//            locationManager = null;
//        }
        unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
        stopService(intent);

 }



}