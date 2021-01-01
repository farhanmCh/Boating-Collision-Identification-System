package ca.carleton.boatingcollisionidentificationsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class LiDAR extends MainMenu {
    private static final String TAG = "LiDAR";
    Button btn;
    ImageView image;
    TextView newmessage;
    String data;
    String arr = null;
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

        final Python py = Python.getInstance();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            Log.d(TAG, "Bundle Data: " + bundle.getString("theMessage"));
            data = bundle.getString("theMessage");
            newmessage.setText(data);

            Log.i("pointcloud", data);
            PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
            PyObject funcObj = scriptObj.callAttr("plot", data);    // Invoke plot() function

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

                Log.i("pointcloud", arr);
                PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
                PyObject funcObj = scriptObj.callAttr("plot", arr);    // Invoke plot() function

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
}
