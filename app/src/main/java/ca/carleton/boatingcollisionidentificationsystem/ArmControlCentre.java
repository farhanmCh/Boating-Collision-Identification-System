package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class ArmControlCentre extends AppCompatActivity implements View.OnClickListener{
    Button BTleft;
    Button BTright;
    String TAG = "ARM";
    public static String ARM_CONTROL_LEFT = "com.boating-collision-identification-system.DIRECTION_LEFT";
    public static String ARM_CONTROL_RIGHT = "com.boating-collision-identification-system.DIRECTION_RIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armcontrolcentre);

        BTleft = findViewById(R.id.BTleft);
        BTleft.setOnClickListener(this);

        BTright = findViewById(R.id.BTright);
        BTright.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        BTleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent moveLeft = new Intent(ARM_CONTROL_LEFT);
                sendBroadcast(moveLeft);
                Log.d(TAG,"Left button pressed " );

            }
        });
        BTright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveRight = new Intent(ARM_CONTROL_RIGHT);
                sendBroadcast(moveRight);
                Log.d(TAG,"Right button pressed ");
            }
        });
    }

}
