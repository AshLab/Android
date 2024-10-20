package com.example.waterlevel;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener extends Thread {
    private DatagramSocket socket;
    private byte[] buffer = new byte[1024];

    private boolean running = true;
    private int port=0;

    private TextView topLevel, botLevel;
    private Activity activity;


    public UDPListener(Activity activity, int port, TextView topLevel, TextView botLevel) {
        this.port = port;
        this.running = true;
        this.topLevel = topLevel;
        this.botLevel = botLevel;
        this.activity = activity;

        Log.d("UDPListener", "UDPListener created");


    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                Log.d("UDPListenerThread", "Received message: " + message);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (message.equals("3"))
                        {
                            topLevel.setText("1");
                            botLevel.setText("1");
                        }

                        else if (message.equals("2"))
                        {
                            topLevel.setText("1");
                            botLevel.setText("0");
                        }
                        else if (message.equals("1"))
                        {
                            topLevel.setText("0");
                            botLevel.setText("1");
                        }

                        else
                        {
                            topLevel.setText("0");
                            botLevel.setText("0");
                        }

                    }
                });

                Thread.sleep(2000);


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
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        Log.d("UDPListener", "UDP listener stopped");
    }

}