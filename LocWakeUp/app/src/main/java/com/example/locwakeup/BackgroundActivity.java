package com.example.locwakeup;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class BackgroundActivity extends Service {

    //Log Tag
    private static final String TAG = BackgroundActivity.class.getSimpleName();

    private PowerManager.WakeLock wakeLock;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location targetLocation;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    //Variables and Configuration
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private long locUpdateDelay = 1000;
    private float locMinDistance = 1;

    private double destinationLatitude = 0;
    private double destinationLongitude = 0;

    private float alarmDistance = 10;
    private long vibrationTime = 500;

    private int alarmStatus = 0;

    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "onCreate");

        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.drum);

        Log.d(TAG, "onCreate: Loc,Vibrator & MediaPlayer initialized");

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BackgroundActivity:WakeLock");
        wakeLock.acquire(10*60*1000L*60 /*600 minutes*/);

        Log.d(TAG, "onCreate: WakeLock acquired");
    }

    public int onStartCommand(Intent intent, int flags, int startId)
        {

            Log.d(TAG, "onStartCommand");

            locUpdateDelay = intent.getLongExtra("locUpdateDelay", 1000);
            locMinDistance = intent.getLongExtra("locMinDistance", 1);

            destinationLatitude= intent.getDoubleExtra("destinationLatitude", 0);
            destinationLongitude= intent.getDoubleExtra("destinationLongitude", 0);

            alarmDistance = intent.getFloatExtra("alarmDistance", 10);
            vibrationTime = intent.getLongExtra("vibrationTime", 500);

            targetLocation.setLatitude(destinationLatitude);
            targetLocation.setLongitude(destinationLongitude);

            //Set AlarmStatus to 0
            alarmStatus = 0;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "onStartCommand: Location Permission not granted");
            }

            else
            {
                startLocationUpdates();
                Log.d(TAG, "onStartCommand: Location Permission granted");
            }


        return START_STICKY;
    }


    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locUpdateDelay, locMinDistance, locationListener);

        }

        locationListener=new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {

                startAlarm(calculateDistance(location));

            }
        };

    }

    private float calculateDistance(Location location)
    {
        return(location.distanceTo(targetLocation));


    }

    private void startAlarm(float distance)
    {
        if( distance < alarmDistance && alarmStatus == 0)
        {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrationTime,VibrationEffect.DEFAULT_AMPLITUDE));

            if (mediaPlayer != null) {
                mediaPlayer.start();
            }

            alarmStatus = 1; //Alarm once
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (locationManager != null && locationListener != null)
        {
            locationManager.removeUpdates(locationListener);
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if ( wakeLock != null && wakeLock.isHeld())
        {
            wakeLock.release();
        }
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
