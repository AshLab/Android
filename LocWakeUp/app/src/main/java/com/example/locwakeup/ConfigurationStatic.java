package com.example.locwakeup;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public  class ConfigurationStatic
{

    //Default Values

    public static long locUpdateDelay=1000;  //in milli seconds
    public static float locMinDistance=1;  //in meters

    public static float alarmDistance = 300; //in meters

    public static long vibrationTime = 20000;

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
    }


}
