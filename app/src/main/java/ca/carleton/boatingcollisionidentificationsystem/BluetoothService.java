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
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;


public class BluetoothService extends Service {
    public String TAG = "BlueTooth";
    public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothDevice hc05 = mBluetoothAdapter.getRemoteDevice("00:14:03:05:08:80");
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
        filter.addAction(HardwareInfo.BATTERY_MONITOR_START);

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
                BufferedReader br = null;
                int bytes;
                String[][] arr = new String[102][201];
                int idx = 0;
                int idx_new;
                int idy = 0;
                StringBuilder sb = new StringBuilder();

                String incomingMessage = "";
                while (true) {
                    int count = 0;
                    Log.d(TAG, "Entered While loop");
                    try {
                        idx = 0;
                        Log.d(TAG, "Entered Try");
                        // Keep listening to the InputStream until an exception occurs
                        int msg;
                        // Read from the InputStream
                        br = new BufferedReader(new InputStreamReader(mmInputStream));
                        incomingMessage = br.readLine();
                        Log.d(TAG, "InputStream1: " + incomingMessage);


                        while (!incomingMessage.contains("-20000") && idy != arr.length) {
                            if (incomingMessage.contains("-10000")) {
                                //Log.d(TAG, "idx0: " + idx + ", idy0: " + idy);
                                while (idx < arr[0].length){
                                    Log.d(TAG, "second loop. idx:  " + idx);
                                    arr[idy][idx] = "0";
                                    idx += 1;
                                }
                                Log.d(TAG, "New line");
                            }else if(incomingMessage.length() < 6){
                                Log.d(TAG, "Bad data");
                                Log.d(TAG, "idx2: " + idx + ", idy2: " + idy);
                            }else{
                                idx_new = Integer.parseInt(incomingMessage.substring(0,3));
                                if (idx > idx_new){
                                    while(idx < arr[0].length){
                                        arr[idy][idx] = "0";
                                        idx += 1;
                                        Log.d(TAG, "first loop. idx: " + idx + ", idx_new: " + idx_new);
                                    }
                                    idy += 1;
                                    idx = 0;
                                }
                                while (idx < idx_new){
                                    Log.d(TAG, "second loop. idx:  " + idx+ ", idx_new: " + idx_new);
                                    arr[idy][idx] = "0";
                                    idx += 1;
                                }

                                arr[idy][idx] =  Integer.toString(Math.min(Math.max(Integer.parseInt(incomingMessage.substring(3,6).replaceFirst("^0+(?!$)", "")), 0),200));
                                idx += 1;
                            }

                            Log.d(TAG, "InputStream2: " + incomingMessage);
                            br = new BufferedReader(new InputStreamReader(mmInputStream));
                            incomingMessage = br.readLine();
                            count++;
                            Log.d(TAG, "InputStream3: " + incomingMessage);
                        }

                        Log.d(TAG, "idxFinal: " + idx + ", idyFinal: " + idy);
                        Log.d(TAG, "DataStream " + Arrays.deepToString(arr));
                        Intent incomingBattVIntent = new Intent(BluetoothService.this, HardwareInfo.class);
                        incomingBattVIntent.putExtra("theVoltage", Arrays.deepToString(arr));
                        Intent incomingMessageIntent = new Intent(BluetoothService.this, LiDAR.class);
                        incomingMessageIntent.putExtra("theMessage", Arrays.deepToString(arr));

                        if(count <=2){
                            incomingBattVIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            incomingBattVIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(incomingBattVIntent);
                        }else{
                            incomingMessageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            incomingMessageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear top of the stack activity (previous LiDAR page)
                            startActivity(incomingMessageIntent);
                        }


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
            }else if(Instruction.equals(HardwareInfo.BATTERY_MONITOR_START)){
                try {
                    mmOutputStream.write("3\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
