package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AppSecurity extends AppCompatActivity {
    public Button buttonBluetoothSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettings);

        buttonBluetoothSettings = findViewById(R.id.Bluesettings);                                    //Bluetooth Settings

        buttonBluetoothSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS );
                startActivity(intent);
            }
        });
    }
}
