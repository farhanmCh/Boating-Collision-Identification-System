package ca.carleton.boatingcollisionidentificationsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        Button buttonMaps = (Button) findViewById(R.id.gps);
        buttonMaps.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);                                                       // ToolBar
        setSupportActionBar(toolbar);

        BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:0A:0D");

        startService(new Intent(this,BluetoothService.class));

    }
    public void onClick(View v){
        char[] test = {'\n','b','a'};
        //Begin Bluetooth Connection

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
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, BluetoothService.class));
    }
}

