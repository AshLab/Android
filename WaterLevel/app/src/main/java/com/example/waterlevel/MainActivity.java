package com.example.waterlevel;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import com.example.waterlevel.UDPListener;

public class MainActivity extends AppCompatActivity {



    TextView topLevel, midLevel, botLevel;
    TextView timeTX;
    Switch alarmSW;

    private Timer timer;
    private Instant curTime, updatedTime;
    private Duration duration;

    int port = 4000;
    UDPListener udpListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize UI
        topLevel = findViewById(R.id.topLevel);
        midLevel = findViewById(R.id.midLevel);
        botLevel = findViewById(R.id.botLevel);
        alarmSW = findViewById(R.id.alarmSW);
        timeTX = findViewById(R.id.timeTX);


        //initialize UDP Listener
         udpListener = new UDPListener(this, port, topLevel, midLevel, botLevel);

         initializeSlideSwitch();
         //Start UDP Listener in a separate thread
         udpListener.start();




        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateTimer();
                    }
                });
            }

        }, 0, 5000);



        }

        void initializeSlideSwitch()
        {
            alarmSW.setChecked(false);
            udpListener.setSlidervalue(0);

            //Creating Callback for Alarm Switch
            alarmSW.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    udpListener.setSlidervalue(1);
                    udpListener.initializeMediaPlayer();
                }

                else {
                    udpListener.setSlidervalue(0);
                    udpListener.stopAlarm();
                }
            });

        }


    void updateTimer()
    {
        curTime = Instant.now();
        updatedTime = udpListener.getUpdatedTime();

        duration = Duration.between(updatedTime, curTime);
        timeTX.setText(String.valueOf(duration.getSeconds()));

        Log.d("MainActivity", "updateTimer() called");


    }



    @Override
        protected void onDestroy() {
            super.onDestroy();

            if(timer != null)
                timer.cancel();

            // Stop the UDP listener when the activity is destroyed
            udpListener.stopListening();
            Log.d("MainActivity", "onDestroy() called");
        }




    }

