package com.example.vibrator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.button);
        textView=findViewById(R.id.textView);

        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayer=MediaPlayer.create(this,R.raw.drum);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(String.valueOf(v.getId()));
                vibrator.vibrate(VibrationEffect.createOneShot(10000, 100));

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });
    }

    @Override
   protected void onDestroy()
    {
        super.onDestroy();
        // Release the MediaPlayer resource when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}