package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    public Button buttonUnlock;

    //Launch screen activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        buttonUnlock = (Button) findViewById(R.id.BTunlock);

        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,home_2.class);
                startActivity(intent);
            }
        });
    }
}
