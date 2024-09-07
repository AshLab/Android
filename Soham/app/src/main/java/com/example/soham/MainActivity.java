package com.example.soham;

import static android.util.Log.d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textDesLat, textDesLong, textCurLat, textCurLong, textDist, textDuration;
    private Switch aSwitch;

    private Button  button, lastUpdateButton, grabButton;

    private Spinner locSpinner;;

    private BroadcastReceiver broadcastReceiver;
    private Location updatedLocation=null;

   // private Handler handler;
   // private Runnable runnable;

    //Static Codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    ConfigurationStatic configurationStatic = new ConfigurationStatic();

//    //Internal Variables
//
//    private long locUpdateDelay=1000;  //in milli seconds
//    private float locMinDistance=1;  //in meters
//
//    private float alarmDistance = 300; //in meters
//
//    private long vibrationTime = 20000;



    // ********* GTP *********
   // private double destinationLat = 12.926235;
   // private double destinationLong= 77.683202;

    // ********* Apollo Marathahalli *********
    private double destinationLat = 12.9560330;
    private double destinationLong= 77.71705380;


    private float distance ;
    private int switchStatus;
    {
        switchStatus = 0;
    }

    private Instant currentTime;
    private Instant updatedTime = Instant.now();
    private Duration duration;

    private final int uiactive=1;
    private final int uiinactive=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get UI Objects
        textCurLat=findViewById(R.id.textCurLat);
        textCurLong=findViewById(R.id.textCurLong);
        textDesLat=findViewById(R.id.textDesLat);
        textDesLong=findViewById(R.id.textDesLong);

        textDist=findViewById(R.id.distance);
        textDuration=findViewById(R.id.duration);
        aSwitch=findViewById(R.id.locEnable);

        button=findViewById(R.id.buttonAdd);
        lastUpdateButton=findViewById(R.id.lastUpdateButton);
        grabButton=findViewById(R.id.grabButton);

        locSpinner=findViewById(R.id.locSpinner);


        //Update Destination Lat and Long
        textDesLat.setText("Lat " + destinationLat);
        textDesLong.setText("Long " + destinationLong );


        configurationStatic.initializeConfiguration(this);

        initializelocSpinner();

        //Initialize Slide Switch and Call back
        initializeSlideSwitch();

        initializeButton();

        //initializeHandler();

        initializeBroadcast();


        //Check and Get Permission for Location Service

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }



    }

    private void initializelocSpinner()
    {
        //String[] items = new String[]{"1000", "2000", "3000", "4000", "5000"};
        Log.d(TAG, "initializelocSpinner: ");
        SharedPreferences sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Map<String, ?> locData =sharedPreferences.getAll();

        String[] locKeys = locData.keySet().toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locKeys);
        locSpinner.setAdapter(adapter);

        locSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: " + selectedItem);

                String loc_data= sharedPreferences.getString(selectedItem, null);

                textDesLat.setText(loc_data.split(" ")[1]);
                textDesLong.setText(loc_data.split(" ")[2]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    //Register Switch Callback

    private void initializeSlideSwitch()
    {
        Log.d(TAG, "initializeSlideSwitch: ");
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){

                Log:d(TAG, "initializeSlideSwitch: Switch On");

                switchStatus=1;


                //Get Destination Lat and Long
                destinationLat = Double.parseDouble(textDesLat.getText().toString());
                destinationLong = Double.parseDouble(textDesLong.getText().toString());
                Log.d(TAG, "initializeSlideSwitch: " + destinationLat + " " + destinationLong);
                //Start Service

                Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
                intent.putExtra("destinationLat", destinationLat);
                intent.putExtra("destinationLong", destinationLong);
                intent.putExtra("vibrationTime", ConfigurationStatic.vibrationTime);
                intent.putExtra("alarmDistance", ConfigurationStatic.alarmDistance);
                intent.putExtra("locUpdateDelay", ConfigurationStatic.locUpdateDelay);
                intent.putExtra("locMinDistance", ConfigurationStatic.locMinDistance);

                startService(intent);
                Log.d(TAG, "initializeSlideSwitch: Service Started");

                setUIUpdate(uiactive);

                Log.d(TAG, "Disabling Spinner");
                locSpinner.setEnabled(false);

                currentTime = Instant.now();
                updatedTime = Instant.now();


            }
            else{
                Log:d(TAG, "initializeSlideSwitch: Switch Off");
                switchStatus=0;

                Log.d(TAG, "Enabling Spinner");
                locSpinner.setEnabled(true);

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
                Log.d(TAG, "Config Button Clicked: ");
                Intent intent = new Intent(MainActivity.this, subActivity.class);
                startActivity(intent);
            }
        });

        lastUpdateButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "Last Update Button Clicked: ");
                currentTime = Instant.now();
                duration = Duration.between(updatedTime, currentTime);

                textDuration.setText("Last Updated (s) :  " + duration.getSeconds());
            }
        });

        grabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Grab Button Clicked: ");
                if (updatedLocation != null) {
                    Log.d(TAG, "Grab Button Clicked: Location Not Null");
                    configurationStatic.grabLocation(updatedLocation);
                    Toast.makeText(MainActivity.this, "Location Grabbed", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.d(TAG, "Grab Button Clicked: Location Null");
                    Toast.makeText(MainActivity.this, "Nothing to Grab", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
    private void initializeHandler()
    {
        Log.d(TAG, "initializeHandler: ");
        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {
            @Override
            public void run()
            {
                currentTime = Instant.now();
                duration = Duration.between(updatedTime, currentTime);

                textDuration.setText("Last Updated (s) :  " + duration.getSeconds());

                handler.postDelayed(this, 2000);
            }
        };
    }
*/


    private void updateLocation(Location location, float distance)
   {
       Log.d(TAG, "updateLocation: ");


       double latitude = location.getLatitude();
       double longitude = location.getLongitude();

       updatedTime = Instant.now();


       //Update Current Lat and Long
       textCurLat.setText("Lat " + latitude);
       textCurLong.setText("Long " + longitude);

       updatedLocation=location; //Set the received location to grab

       //Update Distance
       textDist.setText("Dist " + distance);




       //Update Duration

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


protected void onPause(){
        Log.d(TAG, "onPause: ");
        super.onPause();

        setUIUpdate(uiinactive);

        Log:d(TAG, "onPause: " + uiinactive);


        unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "onPause: Broacast Unregistered");

}

protected void onResume()
{
    Log.d(TAG, "onResume: ");
        super.onResume();
        setUIUpdate(uiactive);

        Log:d(TAG, "onResume: " + uiactive);

        initializeBroadcast();
        Log.d(TAG, "onResume: Broacast Registered");

}


 protected void onDestroy()
 {
     Log.d(TAG, "onDestroy: ");
        super.onDestroy();

        //if(broadcastReceiver!=null)
         //   unregisterReceiver(broadcastReceiver);

        Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
        stopService(intent);


 }

 private void setUIUpdate(int isActive)
 {
     Log.d(TAG, "setUIUpdate:" + isActive);

     Intent intent = new Intent("mainactivity_state");
     intent.putExtra("isActive", isActive);
     LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
 }



}