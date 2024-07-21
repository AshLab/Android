package com.example.locwakeup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class subActivity extends AppCompatActivity {

    //Initialize button
    Button setbutton, addButton,readButton;
    TextView userNameText, userLatText, userLongText;
    TextView locDelayText, locDistanceText, alarmDistanceText,vibDelayText;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ConfigurationStatic configurationStatic = new ConfigurationStatic();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d("subActivity", "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.subactivity);

        setbutton= findViewById(R.id.setButton);
        addButton= findViewById(R.id.addButton);
        readButton= findViewById(R.id.readButton);

        userNameText= findViewById(R.id.userNameText);
        userLatText= findViewById(R.id.userLatText);
        userLongText= findViewById(R.id.userLongText);

        locDelayText= findViewById(R.id.locDelayText);
        locDistanceText= findViewById(R.id.locDistText);
        alarmDistanceText= findViewById(R.id.alarmDistText);
        vibDelayText= findViewById(R.id.vibTimeText);



        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        configurationStatic.initializeConfiguration(this);

        initializeSetButton();
        initializeAddButton();
        initializeReadButton();

        displayConfiguration();


    }

    private void initializeSetButton()
    {
        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setConfiguration();
            }
        });
    }

    private void initializeAddButton()
    {
        Log.d("subActivity", "initializeAddButton");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("userName", userNameText.getText().toString());
                editor.putString("userLat", userLatText.getText().toString());
                editor.putString("userLong", userLongText.getText().toString());
                editor.commit();

                Toast.makeText(subActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeReadButton() {
        Log.d("subActivity", "initializeReadButton");
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameText.setText(sharedPreferences.getString("userName", ""));
                userLatText.setText(sharedPreferences.getString("userLat", ""));
                userLongText.setText(sharedPreferences.getString("userLong", ""));
            }
        });
    }


    private void displayConfiguration()
    {
      Log.d("subActivity", "displayConfiguration");

      configurationStatic.loadConfig();

      locDelayText.setText(Long.toString(ConfigurationStatic.locUpdateDelay));
      locDistanceText.setText(Float.toString(ConfigurationStatic.locMinDistance));
      alarmDistanceText.setText(Float.toString(ConfigurationStatic.alarmDistance));
      vibDelayText.setText(Long.toString(ConfigurationStatic.vibrationTime));

    }

    private void setConfiguration()
    {
        Log.d("subActivity", "setConfiguration");

        ConfigurationStatic.locUpdateDelay = Long.parseLong(locDelayText.getText().toString());
        ConfigurationStatic.locMinDistance = Float.parseFloat(locDistanceText.getText().toString());
        ConfigurationStatic.alarmDistance = Float.parseFloat(alarmDistanceText.getText().toString());
        ConfigurationStatic.vibrationTime = Long.parseLong(vibDelayText.getText().toString());

        configurationStatic.commitConfig();

        Toast.makeText(subActivity.this, "Configuration Saved", Toast.LENGTH_SHORT).show();

    }

}
