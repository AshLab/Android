package com.example.locwakeup;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
    private static final String CHANNEL_ID = "LocationServiceChannel";

    private PowerManager.WakeLock wakeLock;
    private LocationManager locationManager;
    //private LocationListener locationListener;
    private Location targetLocation;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    ConfigurationStatic configurationStatic = new ConfigurationStatic();

    //Variables and Configuration
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private long locUpdateDelay = 5000;
    private float locMinDistance = 100;

    private double destinationLatitude = 0;
    private double destinationLongitude = 0;

    private float alarmDistance = 10;
    private long vibrationTime = 500;

    private int alarmStatus = 0;

      public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "onCreate");

        NotificationManager notificationManager;

        notificationManager=createNotificationChannel();

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking location in the background")
                .setSmallIcon(R.drawable.ic_location)
                .build();
       // notificationManager.notify(1, notification);
        startForeground(1, notification);

        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.drum);

        Log.d(TAG, "onCreate: Loc,Vibrator & MediaPlayer initialized");

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BackgroundActivity:WakeLock");
        wakeLock.acquire(10*60*60*1000L /*600 minutes*/);

        Log.d(TAG, "onCreate: WakeLock acquired");
    }

    public int onStartCommand(Intent intent, int flags, int startId)
        {

            Log.d(TAG, "onStartCommand");

            targetLocation = new Location("");

            locUpdateDelay = intent.getLongExtra("locUpdateDelay", 1000);
            locMinDistance = intent.getFloatExtra("locMinDistance", 1);

            destinationLatitude= intent.getDoubleExtra("destinationLat", 0);
            destinationLongitude= intent.getDoubleExtra("destinationLong", 0);

            alarmDistance = intent.getFloatExtra("alarmDistance", 10);
            vibrationTime = intent.getLongExtra("vibrationTime", 500);

            Log.d(TAG, "onStartCommand: " + locUpdateDelay + " " + locMinDistance + " " + alarmDistance + " " + vibrationTime);

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
                Log.d(TAG, "onStartCommand: Location Permission granted");
                startLocationUpdates();
                Log.d(TAG, "Location Update Started");
            }


        return START_STICKY;
    }


    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locUpdateDelay, locMinDistance, locationListener);
            Log.d(TAG, "startLocationUpdates: Location Permission granted");
        }

    }

    private final LocationListener locationListener=new LocationListener()
    {
        @Override
        public void onLocationChanged(@NonNull Location location)
        {
            float distance = calculateDistance(location);

            startAlarm(distance);
            broadcastLocation(location, distance);

            Log.d(TAG, "onLocationChanged: " + distance);
            Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
            Log.d(TAG, "onLocationChanged: " + targetLocation.getLatitude() + " " + targetLocation.getLongitude());

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };

    private float calculateDistance(Location location)
    {
        return(location.distanceTo(targetLocation));


    }

    private void startAlarm(float distance)
    {
        Log.d(TAG, "startAlarm: " + distance);
        if( distance < alarmDistance && alarmStatus == 0)
        {
            Log.d(TAG, "startAlarm: Alarm Started");
            vibrator.vibrate(VibrationEffect.createOneShot(vibrationTime,VibrationEffect.DEFAULT_AMPLITUDE));

            if (mediaPlayer != null) {
                Log.d(TAG, "startAlarm: MediaPlayer not null");
                mediaPlayer.start();
            }

            alarmStatus = 1; //Alarm once
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (locationManager != null )
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


    private  NotificationManager createNotificationChannel() {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }

        return manager;

    }

    private void broadcastLocation(Location location, float distance)
    {
        Intent intent = new Intent("locWakeUp_location_update");
        intent.putExtra("location", location);
        intent.putExtra("distance", distance);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
