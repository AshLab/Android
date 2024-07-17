package com.example.locwakeup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private TextView textDesLat, textDesLong, textCurLat, textCurLong, textDist;
    private Switch aSwitch;

    private Button  button;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    private LocationManager locationManager;

    private Location targetLocation = new Location("");

    //Static Codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //Internal Variables

    private long locUpdateDelay=5000;  //in milli seconds
    private float locMinDistance=10;  //in meters

    private float alarmDistance = 1000; //in meters

    private long vibrationTime = 10000;

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
        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer=MediaPlayer.create(this,R.raw.drum);

        //Update Destination Lat and Long
        textDesLat.setText("Lat " + destinationLat);
        textDesLong.setText("Long " + destinationLong );

        //Set Target Locations to calculate distance
        targetLocation.setLatitude(destinationLat);
        targetLocation.setLongitude(destinationLong);


        //Initialize Slide Switch and Call back
        initializeSlideSwitch();

        initializeButton();


        //Check and Get Permission for Location Service

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }



    }

    //Register Switch Callback

    private void initializeSlideSwitch()
    {
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, subActivity.class);
                startActivity(intent);
            }
        });
    }


    //Registering Location Listener
    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locUpdateDelay, locMinDistance, locationListener);

        }

    }

    //Location Listener Callback
//    private final LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(@NonNull Location location) {
//
//            updateLocation(location);
//            calculateDistance(location);
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//
//        @Override
//        public void onProviderEnabled(@NonNull String provider) {
//        }
//
//        @Override
//        public void onProviderDisabled(@NonNull String provider) {
//        }
//    };

   private void updateLocation(Location location)
   {
       double latitude = location.getLatitude();
       double longitude = location.getLongitude();

       //Update Current Lat and Long
       textCurLat.setText("Lat " + latitude);
       textCurLong.setText("Long " + longitude);
   }

   private void calculateDistance(Location location)
   {
    distance =location.distanceTo(targetLocation);

    textDist.setText("Dis " + distance);

    //if(switchStatus==1)
    //{
      //  startAlarm();
    //}
   }

//    private void startAlarm()
//    {
//        if( distance < alarmDistance)
//        {
//            vibrator.vibrate(VibrationEffect.createOneShot(vibrationTime,VibrationEffect.DEFAULT_AMPLITUDE));
//
//            if (mediaPlayer != null) {
//                mediaPlayer.start();
//            }
//
//            switchStatus=0; //Alarm once
//        }
//    }


 protected void onDestroy()
 {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
//
//        if (locationManager != null) {
//            locationManager.removeUpdates(locationListener);
//            locationManager = null;
//        }

        Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
        stopService(intent);
 }



}