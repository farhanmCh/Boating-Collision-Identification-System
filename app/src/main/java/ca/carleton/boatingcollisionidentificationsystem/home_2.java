package ca.carleton.boatingcollisionidentificationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class home_2 extends AppCompatActivity {
    public Button buttonUnlock;

    //Actual PIN number
    public String password;
    // User Entered PIN Number
    EditText PinNumber;




    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_2);
        buttonUnlock = findViewById(R.id.BTunlock);
        PinNumber = findViewById(R.id.pinNumber);
        SharedPreferences settings  = getSharedPreferences("PREFS", 0);
        password = settings.getString("password","0000");

        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPinString = PinNumber.getText().toString();
                if(userPinString.equals(password)){
                    Intent intent = new Intent(home_2.this,MainMenu.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(home_2.this, "Wrong PIN Number!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
