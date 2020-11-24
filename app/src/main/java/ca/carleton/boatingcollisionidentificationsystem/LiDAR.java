package ca.carleton.boatingcollisionidentificationsystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;



import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class LiDAR extends MainMenu {
    Button btn;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lidar);

        btn = (Button) findViewById(R.id.button2);
        image = (ImageView) findViewById(R.id.image2);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        final Python py = Python.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PyObject scriptObj = py.getModule("lidarsoftware"); // Read python script
                PyObject funcObj = scriptObj.callAttr("plot");    // Invoke plot() function


                String str = funcObj.toString();
                byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                image.setImageBitmap(bitmap);

            }
        });
    }
}
