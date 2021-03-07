package ca.carleton.boatingcollisionidentificationsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import ca.carleton.boatingcollisionidentificationsystem.env.ImageUtils;
import ca.carleton.boatingcollisionidentificationsystem.tflite.Detector;
import ca.carleton.boatingcollisionidentificationsystem.tflite.TFLiteObjectDetectionAPIModel;

public class LiDAR extends AppCompatActivity {
    private static final String TAG = "LiDAR";
    Button StartBtn;
    Button StopBtn;
    Button MLstart;
    Button ResetBtn;
    ImageView image;
    TextView newmessage;
    String data;
    String arr = null;
    public String finalMsg;

    TextView detectedObjects;

    private Detector detector;
    private enum DetectorMode {
        TF_OD_API;
    }
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "lidarDetect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "label_map.txt";
    private static final LiDAR.DetectorMode MODE = LiDAR.DetectorMode.TF_OD_API;
    public static String LIDAR_MONITOR_START = "com.boating-collision-identification-system.LIDAR_MONITOR_START";
    public static String LIDAR_MONITOR_STOP = "com.boating-collision-identification-system.LIDAR_MONITOR_STOP";

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lidar);

        StartBtn = (Button) findViewById(R.id.button2);
        StopBtn =  (Button) findViewById(R.id.button4);
        MLstart = (Button) findViewById(R.id.mlstart);
        ResetBtn = (Button) findViewById(R.id.resetbt);
        image = (ImageView) findViewById(R.id.image2);
        newmessage = (TextView) findViewById(R.id.Newmessage);
        detectedObjects = (TextView) findViewById(R.id.ObjectName);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        final Python py = Python.getInstance();

        int cropSize = TF_OD_API_INPUT_SIZE;
        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            this,
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            System.out.println("ERRRRRRRRRRORR: Here");
            e.printStackTrace();
        }

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


        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiDAR.this, LiDAR.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        MLstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



                try {
                    //Bitmap bitmap = getBitmapFromAssets("bikefront17.png");
                    String str = funcObj.toString();
                    byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                            bitmap, 300, 300, false);
                    System.out.println("Bitmap Config:" + bitmap.getConfig().toString());
                    int previewWidth = resizedBitmap.getWidth();
                    int previewHeight = resizedBitmap.getHeight();

                    int myCropsize=300;
                    frameToCropTransform =
                            ImageUtils.getTransformationMatrix(
                                    previewWidth, previewHeight,
                                    myCropsize, myCropsize,
                                    0, MAINTAIN_ASPECT);

                    cropToFrameTransform = new Matrix();
                    frameToCropTransform.invert(cropToFrameTransform);

                    croppedBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(croppedBitmap);
                    canvas.drawBitmap(resizedBitmap, frameToCropTransform, null);

                    //image.setImageBitmap(bitmap);

                    //Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    //       bitmap, 300, 300, false);


                    //Bitmap resizedBitmap = resizeBitmap(bitmap, 300);

                    //Create a new image bitmap and attach a brand new canvas to it
                    //Bitmap tempBitmap = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    // Detection Box Paint:
                    Canvas tempCanvas = new Canvas(resizedBitmap);
                    Paint detectionBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    detectionBoxPaint.setColor(Color.GREEN);
                    detectionBoxPaint.setStrokeWidth(4);
                    detectionBoxPaint.setStyle(Paint.Style.STROKE);
                    detectionBoxPaint.setTextSize(10);

                    // Text Background Paint:
                    Paint textBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    textBackgroundPaint.setColor(Color.GREEN);
                    textBackgroundPaint.setStyle(Paint.Style.FILL);
                    textBackgroundPaint.setStrokeWidth(1);

                    // Text Paint:
                    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    textPaint.setStrokeWidth(1);
                    textPaint.setTextSize(10);
                    textPaint.setColor(Color.BLACK);
                    textPaint.setStyle(Paint.Style.STROKE);



                    //Draw the image bitmap into the cavas
                    tempCanvas.drawBitmap(croppedBitmap, 0, 0, null);
                    final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);
                    //final RectF location = results.get(0).getLocation();
                    //System.out.println("LOCATION:" + results.get(0).toString());
                    //tempCanvas.drawRect(location, myPaint);

                    float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                    ArrayList<String> detectedObjectsList = new ArrayList<String>();

                    for (final Detector.Recognition result : results) {
                        final RectF location = result.getLocation();
                        final Double confidence = (double)Math.round((result.getConfidence()*100)*100d)/100d;
                        final String object = result.getTitle();

                        if (location != null && result.getConfidence() >= minimumConfidence) {
                            detectedObjectsList.add(object + " (" + confidence + "%)");

                            tempCanvas.drawRoundRect(location, 3, 3, detectionBoxPaint);
                            String objectDetectionText = new String(object + " - " + confidence + "%");
                            float w = textPaint.measureText(objectDetectionText);
                            float textSize = textPaint.getTextSize();

                            tempCanvas.drawRoundRect(location.left - 2, location.top - textSize, location.left + w + 5, location.top, 2, 2, textBackgroundPaint);
                            tempCanvas.drawText(objectDetectionText, location.left, location.top, textPaint); //x=300,y=300


                            System.out.println("LOCATION:" + result.toString());
                        }
                    }
                    String detectedObjectsListString = "";
                    for (String i:detectedObjectsList){
                        detectedObjectsListString += i + ",";
                    }
                    detectedObjects.setText(detectedObjectsListString);



                    //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
                    //tempCanvas.drawRoundRect(new RectF(400,500,300,30), 2, 2, myPaint);

                    //LOGGER.info("DoNe");
                    //Attach the canvas to the ImageView
                    image.setImageDrawable(new BitmapDrawable(getResources(), resizedBitmap));


                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
        });
        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startLidar = new Intent(LIDAR_MONITOR_START);
                sendBroadcast(startLidar);


            }
        });

        StopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopLidar = new Intent(LIDAR_MONITOR_STOP);
                sendBroadcast(stopLidar);
            }
        });
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                return result;
            } else {
                int targetWidth = maxLength;

                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                return result;

            }
        }
        catch (Exception e)
        {
            return source;
        }
    }

    // Custom method to get assets folder image as bitmap
    private Bitmap getBitmapFromAssets(String fileName){
        /*
            AssetManager
                Provides access to an application's raw asset files.
        */

        /*
            public final AssetManager getAssets ()
                Retrieve underlying AssetManager storage for these resources.
        */
        AssetManager am = getAssets();
        InputStream is = null;
        try{
            /*
                public final InputStream open (String fileName)
                    Open an asset using ACCESS_STREAMING mode. This provides access to files that
                    have been bundled with an application as assets -- that is,
                    files placed in to the "assets" directory.

                    Parameters
                        fileName : The name of the asset to open. This name can be hierarchical.
                    Throws
                        IOException
            */
            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }

        /*
            BitmapFactory
                Creates Bitmap objects from various sources, including files, streams, and byte-arrays.
        */

        /*
            public static Bitmap decodeStream (InputStream is)
                Decode an input stream into a bitmap. If the input stream is null, or cannot
                be used to decode a bitmap, the function returns null. The stream's
                position will be where ever it was after the encoded data was read.

                Parameters
                    is : The input stream that holds the raw data to be decoded into a bitmap.
                Returns
                    The decoded bitmap, or null if the image data could not be decoded.
        */
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
