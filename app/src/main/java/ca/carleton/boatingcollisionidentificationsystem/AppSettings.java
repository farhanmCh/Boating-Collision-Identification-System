package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AppSettings extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettings);

        Button buttonBluetoothSettings =  (Button) findViewById(R.id.Bluesettings);                                    //Bluetooth Settings
        buttonBluetoothSettings.setOnClickListener(this);
        Button buttonBTback = (Button) findViewById(R.id.BTback);
        buttonBTback.setOnClickListener(this);
        Button buttonAbout = (Button) findViewById(R.id.BtnAboutSet);
        buttonAbout.setOnClickListener(this);

    }
    public void onClick(View v){
        switch(v.getId()){

            //Back button
            case R.id.BTback:
                Intent intent1 = new Intent(AppSettings.this, MainMenu.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;

            //Bluetooth Settings
            case R.id.Bluesettings:
                Intent intent2 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent2);
                break;

            //Bluetooth Settings
            case R.id.BtnAboutSet:
                Intent intent3 = new Intent(AppSettings.this, About.class);
                startActivity(intent3);
                break;
        }
    }
}
