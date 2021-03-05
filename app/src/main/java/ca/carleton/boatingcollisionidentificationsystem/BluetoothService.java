package ca.carleton.boatingcollisionidentificationsystem;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothService extends Service {
    public String TAG = "BlueTooth";
    public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:0A:0D");
    public BluetoothSocket mSocket = null;
    public InputStream mmInputStream;
    public OutputStream mmOutputStream;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ArmControlCentre.ARM_CONTROL_LEFT);
        filter.addAction(ArmControlCentre.ARM_CONTROL_RIGHT);
        filter.addAction(LiDAR.LIDAR_MONITOR_START);
        filter.addAction(LiDAR.LIDAR_MONITOR_STOP);

        registerReceiver(receiver, filter);

        BluetoothSocket tmp = null;
        try {
            tmp = hc05.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mBluetoothAdapter.cancelDiscovery();
        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmInputStream = tmpIn;
        mmOutputStream = tmpOut;
        Log.d(TAG, "InputStream and OutputStream set ");


        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                String incomingMessage = "";
                while (true) {
                    Log.d(TAG, "Entered While loop");
                    try {
                        // Keep listening to the InputStream until an exception occurs
                        String msg = "";
                        // Read from the InputStream
                        bytes = mmInputStream.read(buffer);        // Get number of bytes and message in "buffer"
                        incomingMessage = new String(buffer, 0, bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);

                        while (!incomingMessage.contains("#")) {
                            msg = msg.concat(incomingMessage);
                            bytes = mmInputStream.read(buffer);
                            incomingMessage = new String(buffer, 0, bytes);
                            Log.d(TAG, "InputStream: " + incomingMessage);
                        }
                        Log.d(TAG, "DataStream " + msg);
                        Intent incomingMessageIntent = new Intent(BluetoothService.this, LiDAR.class);
                        incomingMessageIntent.putExtra("theMessage", msg);

                        incomingMessageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        incomingMessageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear top of the stack activity (previous LiDAR page)
                        startActivity(incomingMessageIntent);

                    } catch (IOException e) {
                        break;
                    }
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String Instruction = intent.getAction();
            Log.i(TAG, "Broadcast received");
            assert Instruction != null;
            if(Instruction.equals(ArmControlCentre.ARM_CONTROL_LEFT)){
                try {
                    mmOutputStream.write("l\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(Instruction.equals(ArmControlCentre.ARM_CONTROL_RIGHT)){
                try {
                    mmOutputStream.write("r\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(Instruction.equals(LiDAR.LIDAR_MONITOR_START)){
                try {
                    mmOutputStream.write("1\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(Instruction.equals(LiDAR.LIDAR_MONITOR_STOP)){
                try {
                    mmOutputStream.write("2\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
