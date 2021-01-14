package ca.carleton.boatingcollisionidentificationsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainMenu extends LiDAR implements View.OnClickListener {
    public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "Bluetooth";
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothSocket Socket = null;
    public InputStream inputStream;
    public Context mContext;
    public MainMenu.ConnectThread mConnectThread;

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
        Button buttonArmControl = (Button) findViewById(R.id.armcontrol);
        buttonArmControl.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);                                                       // ToolBar
        setSupportActionBar(toolbar);


    }

    public BluetoothSocket getSocket() {
        return mConnectThread.mmSocket;
    }

    public void onClick(View v){
        char[] test = {'\n','b','a'};
        //Begin Bluetooth Connection
        BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:0A:0D");
        mConnectThread = new MainMenu.ConnectThread(hc05);
        mConnectThread.start();
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

             //Arm Control Centre
            case R.id.armcontrol:
                Intent intent3 = new Intent(MainMenu.this, ArmControlCentre.class);
                startActivity(intent3);
                break;
            default:
                mConnectThread.cancel();
                break;
        }
    }

    public class ConnectThread extends Thread{
        public final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public LiDAR.ConnectedThread mConnectedthreadLIDAR;
        public ArmControlCentre.ConnectedThread2 mConnectedthreadARM;

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
                cancel();
            }
            mConnectedthreadLIDAR = new LiDAR.ConnectedThread(mmSocket);
            mConnectedthreadLIDAR.start();
            try {
                mConnectedthreadARM = new ArmControlCentre.ConnectedThread2(mmSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mConnectedthreadARM.start();
            
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public LiDAR.ConnectedThread getLiDARThread(){
            return mConnectedthreadLIDAR;
        }

        public BluetoothSocket getMmSocket() {
            return mmSocket;
        }
    }
}

