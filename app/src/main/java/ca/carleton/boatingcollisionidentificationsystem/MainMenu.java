package ca.carleton.boatingcollisionidentificationsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.UUID;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter Adapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothSocket Socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        Button buttonMonitor = (Button) findViewById(R.id.mainmonitor);
        buttonMonitor.setOnClickListener(this);
        Button buttonSettings = (Button) findViewById(R.id.AppSet);
        buttonSettings.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);                                                       // ToolBar
        setSupportActionBar(toolbar);
        //InitiateConnection();
    }
    public void onClick(View v){
        switch (v.getId()){

            // LiDAR monitor button event
            case R.id.mainmonitor:
                Intent intent1 = new Intent(MainMenu.this, LiDAR.class);
                startActivity(intent1);
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
    public void InitiateConnection(){
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
    }
}
