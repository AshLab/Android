package com.example.locwakeup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class subActivity extends AppCompatActivity {

    //Initialize button
    Button button, addButton,readButton;
    TextView userNameText, userLatText, userLongText;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.subactivity);

        button= findViewById(R.id.button);
        addButton= findViewById(R.id.addButton);
        readButton= findViewById(R.id.readButton);

        userNameText= findViewById(R.id.userNameText);
        userLatText= findViewById(R.id.userLatText);
        userLongText= findViewById(R.id.userLongText);


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initializeBackButton();
        initializeAddButton();
        initializeReadButton();


    }

    private void initializeBackButton()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(subActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeAddButton()
    {
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
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameText.setText(sharedPreferences.getString("userName", ""));
                userLatText.setText(sharedPreferences.getString("userLat", ""));
                userLongText.setText(sharedPreferences.getString("userLong", ""));
            }
        });
    }
}
