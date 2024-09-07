package com.example.soham;

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
    Button setbutton, addButton,readButton,deleteButton;
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
        deleteButton= findViewById(R.id.deleteButton);

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

        writeDefaultLocation();

        initializeSetButton();
        initializeAddButton();
        initializeReadButton();
        initializeDeleteButton();

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
            public void onClick(View v)
            {
                Log.d("subActivity", "onClick");
               String userName= userNameText.getText().toString().replace(" ", "_");
               String userLat= userLatText.getText().toString();
               String userLong= userLongText.getText().toString();
               editor.putString(userName, userName + " " + userLat + " " + userLong);

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
                Log.d("subActivity", "onClick");
                String userName= userNameText.getText().toString();


                String storedLocation = sharedPreferences.getString(userName, "");

                if (storedLocation.isEmpty())
                {
                    Toast.makeText(subActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();

                }

                else
                {
                    String[] location = storedLocation.split(" ");

                    userNameText.setText(location[0]);
                    userLatText.setText(location[1]);
                    userLongText.setText(location[2]);
                }
            }
        });
    }

    private void initializeDeleteButton()
    {
        Log.d("subActivity", "initializeDeleteButton");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("subActivity", "onClick");
                String userName= userNameText.getText().toString();
                String userName_ext = sharedPreferences.getString(userName, "");
                if (userName_ext.isEmpty())
                {
                    Toast.makeText(subActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    editor.remove(userName);
                    editor.commit();
                    Toast.makeText(subActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }



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

      userLatText.setText(configurationStatic.getGrabbedLat());
      userLongText.setText(configurationStatic.getGrabbedLong());


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

    private void writeDefaultLocation()
    {
        Log.d("subActivity", "writeDefaultLocation");

        String userName= "Office";
        String userLat= "12.926235";
        String userLong= "77.683202";
        String userName_ext="";

        userName_ext= sharedPreferences.getString(userName, "");

        if (userName_ext.isEmpty()) {
            Log.d("subActivity", "OfficeDefaultLocation");
            editor.putString(userName, userName + " " + userLat + " " + userLong);
            editor.commit();
        }

        userName= "Home";
        userLat= "10.408850962";
        userLong= "77.95965891";
        userName_ext= sharedPreferences.getString(userName, "");

        if (userName_ext.isEmpty()){
            Log.d("subActivity", "HomeDefaultLocation");
            editor.putString(userName, userName + " " + userLat + " " + userLong);
            editor.commit();
        }


    }

}
