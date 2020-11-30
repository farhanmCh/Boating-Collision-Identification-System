/*package ca.carleton.boatingcollisionidentificationsystem;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import ca.carleton.boatingcollisionidentificationsystem.R;

public class Terminal extends AppCompatActivity {
    public BluetoothAdapter mBluetoothAdapter;
    static Handler mHandler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal);


        TextView data = (TextView) findViewById(R.id.textView3);

        // Checks if bluetooth device is enabled
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            System.out.println("Device doesn't support Bluetooth");
        }

        //Asks to turn on Bluetooth
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        //BluetoothDevice mDevice = device;
        BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:0A:0D");

        ConnectThread mConnectThread = new ConnectThread(hc05);
        mConnectThread.start();


        mHandler = new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                public void handleMessage(Message msg){
                    byte[] writeBuf = (byte[]) msg.obj;
                    int begin = (int)msg.arg1;
                    int end = (int)msg.arg2;

                    switch(msg.what){
                        case 1:
                            String writeMessage = new String(writeBuf);
                            writeMessage = writeMessage.substring(begin, end);
                            break;
                    }
                }
            }
        })

    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run(){
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private InputStream mmInputStream;
        private OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e){
                e.printStackTrace();
            }
            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
        }
       public void run{
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true){
                try {
                    bytes += mmInputStream.read(buffer, bytes, buffer.length-bytes);
                    for(int i = begin; i < bytes; i++){
                        if(buffer[i] == "#".getBytes()[0]){
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes-1){
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e){
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                mmOutputStream.write(bytes);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

}*/

