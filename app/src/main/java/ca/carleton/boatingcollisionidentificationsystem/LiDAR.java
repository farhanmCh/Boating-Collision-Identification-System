package ca.carleton.boatingcollisionidentificationsystem;

import android.annotation.SuppressLint;
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
import java.io.IOException;

import java.io.InputStreamReader;


public class LiDAR extends AppCompatActivity {
    private static final String TAG = "LiDAR";
    Button btn;
    ImageView image;
    @SuppressLint("StaticFieldLeak")
    static TextView newmessage;
    public static Python py = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lidar);

        btn = (Button) findViewById(R.id.button2);
        image = (ImageView) findViewById(R.id.image2);
        newmessage = (TextView) findViewById(R.id.Newmessage);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(LiDAR.this));
        }

        py = Python.getInstance();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            Log.d(TAG, "Bundle Data: " + bundle.getString("theMessage"));
            String data = bundle.getString("theMessage");
            newmessage.setText(data);
        }

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
}
