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

public class LiDAR extends MainMenu {
    private static final String TAG = "LiDAR";
    Button btn;
    ImageView image;
    TextView newmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lidar);

        btn = (Button) findViewById(R.id.button2);
        image = (ImageView) findViewById(R.id.image2);
        newmessage = (TextView) findViewById(R.id.Newmessage);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            Log.d(TAG, "Bundle Data: " + bundle.getString("theMessage"));
            String data = bundle.getString("theMessage");
            newmessage.setText(data);
        }

        //newmessage.setText("Lidar");
        /*BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra("theMessage");
                //Log.d(TAG, "LiDAR Data: ",text);
                newmessage.setText(text);
            }
        };*/
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        final Python py = Python.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
                PyObject funcObj = scriptObj.callAttr("plot");    // Invoke plot() function



                try {
                    String str = funcObj.toString();
                    byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

    }
}
