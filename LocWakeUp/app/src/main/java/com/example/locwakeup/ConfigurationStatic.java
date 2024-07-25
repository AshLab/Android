package com.example.locwakeup;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;


public  class ConfigurationStatic
{

    //Default Values

    public static long locUpdateDelay=1000;  //in milli seconds
    public static float locMinDistance=1;  //in meters

    public static float alarmDistance = 300; //in meters

    public static long vibrationTime = 20000;

    private String grabbedLat;
    private String grabbedLong;

    SharedPreferences sharedPreferences;



    public void initializeConfiguration(Context context)
    {
        Log.d("ConfigurationStatic","Constructor");
        sharedPreferences = context.getSharedPreferences("config", MODE_PRIVATE);
        loadConfig();

    }

    public void commitConfig()
    {
        Log.d("ConfigurationStatic","commitConfig");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("locUpdateDelay",locUpdateDelay);
        editor.putFloat("locMinDistance",locMinDistance);
        editor.putFloat("alarmDistance",alarmDistance);
        editor.putLong("vibrationTime",vibrationTime);
        editor.apply();
    }

    public void loadConfig()
    {
        Log.d("ConfigurationStatic","loadConfig");
        locUpdateDelay = sharedPreferences.getLong("locUpdateDelay",locUpdateDelay);
        locMinDistance = sharedPreferences.getFloat("locMinDistance",locMinDistance);
        alarmDistance = sharedPreferences.getFloat("alarmDistance",alarmDistance);
        vibrationTime = sharedPreferences.getLong("vibrationTime",vibrationTime);

        grabbedLat = sharedPreferences.getString("grabbed_latitude","");
        grabbedLong = sharedPreferences.getString("grabbed_longitude","");


    }

    public void grabLocation(Location location)
    {
        Log.d("ConfigurationStatic","grabLocation");
        if (location == null)
        {
            Log.d("ConfigurationStatic","grabLocation: location is null");
            return;
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("grabbed_latitude",String.valueOf(location.getLatitude()));
            editor.putString("grabbed_longitude",String.valueOf(location.getLongitude()));
            editor.apply();
        }
    }

    public String getGrabbedLat()
    {
        return grabbedLat;
    }

    public String getGrabbedLong()
    {
        return grabbedLong;
    }

}
