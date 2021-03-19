package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HardwareInfo extends AppCompatActivity {

    public static String BATTERY_MONITOR_START = "com.boating-collision-identification-system.BATTERY_MONITOR_START";
    public String data;
    private static final String TAG = "Battery";
    public Button VTupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hardwareinfo);
        VTupdate = (Button) findViewById(R.id.btupdate);

        TextView BattVoltage = (TextView) findViewById(R.id.batteryVTvalue);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            Log.d(TAG, "Bundle Data: " + bundle.getString("theVoltage"));
            data = bundle.getString("theVoltage");
            BattVoltage.setText(data + " V");
        }
        VTupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateBattery = new Intent(BATTERY_MONITOR_START);
                sendBroadcast(updateBattery);
            }
        });
    }

}
