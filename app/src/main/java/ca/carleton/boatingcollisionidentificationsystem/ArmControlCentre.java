package ca.carleton.boatingcollisionidentificationsystem;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;


public class ArmControlCentre extends AppCompatActivity implements View.OnClickListener{
    Button BTleft;
    Button BTright;
    String TAG = "ARM";
    String left = "l\n";
    String right = "r\n";
    String init = "*";
    public BluetoothSocket socket = null;
    public OutputStream finalTmpOut;
    public static String Direction = "*\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armcontrolcentre);

        BTleft = findViewById(R.id.BTleft);
        BTright = findViewById(R.id.BTright);

    }


    @Override
    public void onClick(View v) {
        BTleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Direction = "l";
            }
        });
        BTright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Direction = "r";
            }
        });
    }

    public class ConnectedThread2 extends Thread{
        public final BluetoothSocket mmSocket;
        public OutputStream mmOutputStream;
        public String mDirection;

        public ConnectedThread2(BluetoothSocket socket) throws IOException {
            mmSocket = socket;
            OutputStream tmpOut;
            tmpOut = socket.getOutputStream();
            mmOutputStream = tmpOut;
        }
        public void run() {
            while(!ConnectedThread2.currentThread().isInterrupted()){
                mDirection = Direction;
                if(mDirection.equals("l\n")){
                    try {
                        mmOutputStream.write(mDirection.getBytes());
                        Log.d(TAG,"Moving left" );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(Direction.equals("r\n")){
                    try {
                        mmOutputStream.write(mDirection.getBytes());
                        Log.d(TAG,"Moving right" );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }Direction = "*";
            }
        }

        public BluetoothSocket getMmSocket() {
            return mmSocket;
        }
    }
}
