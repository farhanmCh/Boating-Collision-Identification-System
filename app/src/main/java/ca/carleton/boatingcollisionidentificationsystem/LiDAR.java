package ca.carleton.boatingcollisionidentificationsystem;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class LiDAR extends ArmControlCentre {
    private static final String TAG = "LiDAR";
    Button btn;
    ImageView image;
    @SuppressLint("StaticFieldLeak")
    static TextView newmessage;
    public static String data;
    String arr = null;
    public String finalMsg;
    public static Python py = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lidar);

        btn = (Button) findViewById(R.id.button2);
        image = (ImageView) findViewById(R.id.image2);
        newmessage = (TextView) findViewById(R.id.Newmessage);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        py = Python.getInstance();

        /*
        PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
        PyObject funcObj = scriptObj.callAttr("plot", finalMsg);    // Invoke plot() function
        String str = funcObj.toString();
        Log.d("pointcloud2", str);
        try {

            byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            image.setImageBitmap(bitmap);
            Log.d("pointcloud3", str);
        }
        catch (Exception e){
            System.out.println(e);
        }*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String arr = null;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(getAssets().open("pointclouds.txt")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    arr = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Log.i("pointcloud", "[[0, 1,2, 3],[0, 1, 2, 3]],[[0, 1, 2, 3], [0, 1, 2, 3]]");
                PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
                PyObject funcObj = scriptObj.callAttr("plot", "[[0, 1,2, 3],[0, 1, 2, 3]],[[0, 1, 2, 3], [0, 1, 2, 3]]");    // Invoke plot() function

                try {
                    String str = funcObj.toString();
                    byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image.setImageBitmap(bitmap);
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
        });
    }

    public class ConnectedThread extends Thread {
        public final BluetoothSocket mmSocket;
        public InputStream mmInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInputStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            String incomingMessage = "";
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(LiDAR.this));
            }
            final Python py = Python.getInstance();
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {

                    String msg="";
                    // Read from the InputStream
                    bytes = mmInputStream.read(buffer);        // Get number of bytes and message in "buffer"
                    incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    while (!incomingMessage.contains("#")){
                        msg = msg.concat(incomingMessage);
                        bytes = mmInputStream.read(buffer);
                        incomingMessage = new String(buffer,0,bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);
                    }
                    Log.d(TAG, "DataStream " + msg);
                    finalMsg = msg;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ReceivedData().setText(finalMsg);
                            Log.d("pointcloud", finalMsg);
                            PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
                            PyObject funcObj = scriptObj.callAttr("plot", finalMsg);    // Invoke plot() function
                            try {
                                String str = funcObj.toString();
                                byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                sleep(500);
                                getPlot().setImageBitmap(bitmap);
                                Log.d("pointcloud3", str);
                            }
                            catch (Exception e){
                                System.out.println(e);
                            }
                            }
                    });
                } catch (IOException e) {
                    break;
                }
            }
        }
        public BluetoothSocket getSocket(){
            return mmSocket;
        }
    }
    public ImageView getPlot(){
        return image;
    }
    public TextView ReceivedData(){
        return newmessage;
    }
}
