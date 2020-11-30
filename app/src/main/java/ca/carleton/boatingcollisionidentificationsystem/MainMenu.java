package ca.carleton.boatingcollisionidentificationsystem;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "Bluetooth";
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothSocket Socket = null;
    public InputStream inputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        //Check if Bluetooth is enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
        }
        Button buttonMonitor = (Button) findViewById(R.id.mainmonitor);
        buttonMonitor.setOnClickListener(this);
        Button buttonSettings = (Button) findViewById(R.id.AppSet);
        buttonSettings.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);                                                       // ToolBar
        setSupportActionBar(toolbar);
        BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:0A:0D");


        MainMenu.ConnectThread mConnectThread = new MainMenu.ConnectThread(hc05);
        mConnectThread.start();


        //InitiateConnection();




    }
    public void onClick(View v){
        char[] test = {'\n','b','a'};
        switch (v.getId()){

            // LiDAR monitor button event
            case R.id.mainmonitor:
                Intent intent1 = new Intent(MainMenu.this, LiDAR.class);
                startActivity(intent1);
                //write(test);
                break;

            // Settings button Event
            case R.id.AppSet:
                Intent intent2 = new Intent(MainMenu.this, AppSettings.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;

        }

        public void run(){
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MainMenu.ConnectedThread mConnectedthread = new MainMenu.ConnectedThread(mmSocket);
            mConnectedthread.start();
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInputStream;
        private OutputStream mmOutputStream;
        Context mContext = getApplicationContext();

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInputStream.read(buffer);        // Get number of bytes and message in "buffer"
                    String incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    Intent incomingMessageIntent = new Intent(MainMenu.this, LiDAR.class);
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    startActivity(incomingMessageIntent);
                    //LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

}
    /*public void InitiateConnection(){
        //Bluetooth Adapter in the phone
        System.out.println(Adapter.getBondedDevices());



        //HC-05 Object
        BluetoothDevice hc05 = Adapter.getRemoteDevice("00:14:03:05:0A:0D");
        System.out.println(hc05.getName());

        //Bluetooth Connection

        try {
            if (Socket == null){
                Socket = hc05.createInsecureRfcommSocketToServiceRecord(mUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                Socket.connect();
                System.out.println(Socket.isConnected());
                Toast.makeText(MainMenu.this, "Device Connected", Toast.LENGTH_LONG).show();



            } else{
                Toast.makeText(MainMenu.this, "Device Unable to Connect", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    //Send Data to bluetooth Module
    /*private void write(char[] ch) {

        try {

            for (char c : ch) {
                new DataOutputStream(Socket.getOutputStream()).writeByte(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    //Receive Data from bluetooth Module
    /*public void read(){

    }*/

