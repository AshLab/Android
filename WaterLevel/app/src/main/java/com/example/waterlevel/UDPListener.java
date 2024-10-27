package com.example.waterlevel;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.metrics.LogSessionId;
import android.util.Log;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;

public class UDPListener extends Thread {
    private DatagramSocket socket;
    private byte[] buffer = new byte[1024];

    private boolean running = true;
    private int port=0;
    private int slidervalue=0;

    private TextView topLevel, midLevel, botLevel;
    private Activity activity;

    private MediaPlayer mediaPlayer;
    private int alarmRepeat = 0;
    private int alarmPlaying=0;


    private Instant updatedTime = Instant.now();


    public UDPListener(Activity activity, int port, TextView topLevel, TextView midLevel,  TextView botLevel) {

        this.port = port;
        this.running = true;

        this.topLevel = topLevel;
        this.midLevel = midLevel;
        this.botLevel = botLevel;

        this.activity = activity;

        initializeMediaPlayer();

        Log.d("UDPListener", "UDPListener created");


    }

    public void initializeMediaPlayer()
    {

        mediaPlayer = MediaPlayer.create(activity,R.raw.alarm);
        //mediaPlayer.setLooping(true);
        alarmRepeat = 0;
        alarmPlaying=0;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                alarmPlaying=0;

                Log.d("UDPListener", "MediaPlayer completed");
            }
        });

        Log.d("UDPListener", "MediaPlayer created");

    }


    public void playAlarm()
    {
        Log.d("UDPListener", "playAlarm() called");
        Log.d("UDPListener", "alarmRepeat: " + alarmRepeat);

        if (alarmRepeat == 0 && mediaPlayer != null)
        {

            mediaPlayer.start();
            alarmPlaying=1;
            alarmRepeat = alarmRepeat + 1;
        }

            Log.d("UDPListener", "MediaPlayer started");

    }

    public void stopAlarm()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        alarmRepeat = 0;
        alarmPlaying=0;

        Log.d("UDPListener", "MediaPlayer stopped");
    }

    public void setSlidervalue(int slidervalue)
    {
        this.slidervalue = slidervalue;


        Log.d("UDPListener", "slider value: " + slidervalue);
        Log.d("UDPListener", "alarmRepeat: " + alarmRepeat);
    }

    public int getSlidervalue()
    {
        return slidervalue;
    }

    public void setUpdatedTime()
    {
        updatedTime = Instant.now();
    }

    public Instant getUpdatedTime()
    {
        return updatedTime;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                setUpdatedTime();

                String message = new String(packet.getData(), 0, packet.getLength());
                Log.d("UDPListenerThread", "Received message: " + message);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (message.equals("3"))
                        {
                            topLevel.setText("1");
                            midLevel.setText("1");
                            botLevel.setText("1");

                        }

                        else if (message.equals("2"))
                        {
                            topLevel.setText("0");
                            midLevel.setText("1");
                            botLevel.setText("1");
                        }
                        else if (message.equals("1"))
                        {
                            topLevel.setText("0");
                            midLevel.setText("0");
                            botLevel.setText("1");
                        }

                        else
                        {
                            topLevel.setText("0");
                            midLevel.setText("0");
                            botLevel.setText("0");
                        }

                    }
                });

                //Check and play alarm
                if (message.equals("3"))
                {
                    if(slidervalue==1)
                    {
                        playAlarm();
                    }

                 //Turn Off Alarm after the water level reaches 3rd stage
                    if(alarmPlaying==0)
                    {
                        setSlidervalue(0);
                    }

                }

                if (message.equals("2"))
                {

                    //Auto set the alarm when the water level reaches 2nd stage
                    setSlidervalue(1);
                    alarmRepeat=0;

                }

                //Thread.sleep(2000);

                Log.d("UDPListenerThread", "slider value: " + slidervalue);

            }
        } catch (Exception e) {
            Log.e("UDPListener run()", "Error in UDP listener thread", e);
            e.printStackTrace();

        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            Log.d("UDPListener - finally", "UDP listener stopped");
        }
    }

    public void stopListening() {
        running = false;
        stopAlarm();

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        Log.d("UDPListener", "UDP listener stopped");
    }

}