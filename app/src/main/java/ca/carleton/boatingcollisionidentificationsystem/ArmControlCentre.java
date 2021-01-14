package ca.carleton.boatingcollisionidentificationsystem;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ArmControlCentre extends AppCompatActivity {
    Button BTleft;
    Button BTright;
    String TAG;
    String left = "l";
    String right = "r";
    String init = "*";
    public BluetoothSocket socket = null;
    public OutputStream finalTmpOut;
    String Direction = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armcontrolcentre);

        BTleft = (Button) findViewById(R.id.BTleft);
        BTright = (Button) findViewById(R.id.BTright);

        /*try {
            socket = getSocket().getRemoteDevice().createRfcommSocketToServiceRecord(MainMenu.mUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Log.d(TAG, "Socket Created");

    }
    public class ConnectedThread2 extends Thread{
        public final BluetoothSocket mmSocket;
        public OutputStream mmOutputStream;

        public ConnectedThread2(BluetoothSocket socket) throws IOException {
            mmSocket = socket;
            OutputStream tmpOut = null;
            tmpOut = socket.getOutputStream();
            mmOutputStream = tmpOut;
        }

        public void run(){
            BTleft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mmOutputStream.write(left.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            BTright.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mmOutputStream.write(right.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
