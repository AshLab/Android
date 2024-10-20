package com.example.waterlevel;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.example.waterlevel.UDPListener;

public class MainActivity extends AppCompatActivity {

    TextView topLevel, botLevel;

    int port = 4000;
    UDPListener udpListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize UI
        topLevel = findViewById(R.id.topLevel);
        botLevel = findViewById(R.id.botLevel);

        //Check and get permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
           // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, 0);
           // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 1);

            Log.d("MainActivity", "Permission granted");
        }

       // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET}, 0);


        //initialize UDP Listener
         udpListener = new UDPListener(this, port, topLevel, botLevel);


         udpListener.start();

        }

//        @Override
//        protected void onDestroy() {
//            super.onDestroy();
//            // Stop the UDP listener when the activity is destroyed
//            udpListener.stopListening();
//            Log.d("MainActivity", "onDestroy() called");
//        }
//
//        protected void onPause() {
//            super.onPause();
//            // Stop the UDP listener when the activity is paused
//            udpListener.stopListening();
//            Log.d("MainActivity", "onPause() called");
//        }
//
//        protected void onResume() {
//            super.onResume();
//              Log.d("MainActivity", "onResume() called");
//            // Start the UDP listener when the activity is resumed
//              udpListener = new UDPListener(this, port, topLevel, botLevel);
//            udpListener.start();

//
//        }


    }

