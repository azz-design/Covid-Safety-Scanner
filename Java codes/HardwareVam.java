package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

public class HardwareVam extends AppCompatActivity {
    private TextureView textureView;
    private ImageView imageView;
    private int noofPerson = -1;
    private Button startButton;
    private Button backButton;
   // public static final String KEY_NOP = "noofpeople";
   // private Bitmap bitmap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

//    private DocumentReference nopRef = db.collection("NOP")
//            .document("First Data");

    private CollectionReference collectionReference = db.collection("NOP");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_vam);

        textureView = findViewById(R.id.view_finder);
        startButton = findViewById(R.id.scanningButton);
        imageView = findViewById(R.id.imageView2);
        backButton = findViewById(R.id.button_back_hardware);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),intro.class);
                startActivity(intent);
                finish();
            }
        });

        textureView.post((Runnable) (new Runnable() {
            public final void run() {
                startCamera();
            }
        }));

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTransform();
            }
        });
    }

    private void startCamera() {
        PreviewConfig.Builder previewConfig = new PreviewConfig.Builder();

        Preview preview = new Preview(previewConfig.build());

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

       // ImageCaptureConfig.Builder imageCaptureConfig = new ImageCaptureConfig.Builder();


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setTargetAspectRatio(new Rational(4,3)).setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
          .build();

        //
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//        myTimer = new Timer();
//
//        myTimer.scheduleAtFixedRate(new TimerTask() {
//
//
//       // @Override
//            public void run() {
//
//                imgCap.takePicture(new ImageCapture.OnImageCapturedListener() {
//                    @Override
//                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
//                        imageView.setImageBitmap(imageProxyToBitmap(image));
//                        Toast.makeText(MainActivity.this, "Sent", Toast.LENGTH_SHORT).show();
//                    }
//
//                });
//            }
//
//        }, 0, 5000);

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                imgCap.takePicture(new ImageCapture.OnImageCapturedListener() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
                         final Bitmap bitmap = imageProxyToBitmap(image);
                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(HardwareVam.this, "Picture Clicked\nNext Picture in 30s", Toast.LENGTH_SHORT).show();

                        showProgress();




                        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                        FirebaseVisionFaceDetectorOptions options =
                                new FirebaseVisionFaceDetectorOptions.Builder()
                                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                        .setMinFaceSize(0.15f)
                                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                        .enableTracking()
                                        .build();

                        FirebaseVisionFaceDetector faceDetector =
                                FirebaseVision.getInstance()
                                        .getVisionFaceDetector(options);

                        faceDetector.detectInImage(firebaseVisionImage)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {

                                        Bitmap mutableImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                                        detectFaces(firebaseVisionFaces, mutableImage);
                                        imageView.setImageBitmap(mutableImage);

                                        addData();

                                        // Toast.makeText(this, " No of People" + noofPerson, Toast.LENGTH_SHORT).show();

                                        Toast.makeText(HardwareVam.this, "No of People : " + noofPerson, Toast.LENGTH_SHORT).show();



                                        hideProgress();




                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(HardwareVam.this, "There was an Error", Toast.LENGTH_SHORT).show();
                                      hideProgress();



                                    }
                                });
                        image.close();

                    }









                });
                // code to execute repeatedly
            }
        }, 0, 30, TimeUnit.SECONDS);

            }
        });


//        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgCap.takePicture(new ImageCapture.OnImageCapturedListener() {
//                    @Override
//                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
//                        imageView.setImageBitmap(imageProxyToBitmap(image));
//                        image.close();
//                    }
//                });
//
//
//
//        }
//    });


        //File file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
//                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
//                    @Override
//                    public void onImageSaved(@NonNull File file) {
//                        String msg = "Pic captured at " + file.getAbsolutePath();
//                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
//                        String msg = "Pic capture failed : " + message;
//                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
//                        if(cause != null){
//                            cause.printStackTrace();
//                        }
//                    }
//                });


//        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
//                .setTargetResolution(new Size(1280, 720))
//                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
//                .build();
//
//        final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
//        imageAnalysis.setAnalyzer(new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(ImageProxy image, int rotationDegrees) {
//
//                final Bitmap bitmap = textureView.getBitmap();
//                image.close();
//
//                if (bitmap == null)
//                    return;
//
////                imageView.setImageBitmap(bitmap);
////                imageView.setRotation(rotationDegrees);
////                image.close();
//
//
//            }
//        });


        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, imgCap);
    }

    private void addData() {
        int nop = noofPerson;
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());


        People people = new People();
        people.setNoofpeople(nop);



        people.setTime(currentDate);


        collectionReference.add(people);
    }


    private void detectFaces(List<FirebaseVisionFace> firebaseVisionFaces, Bitmap bitmap) {

        if(firebaseVisionFaces == null || bitmap == null){
            Toast.makeText(this, "There was an Error", Toast.LENGTH_SHORT).show();
            return;
        }

        Canvas canvas = new Canvas(bitmap);
        Paint facePaint = new Paint();
        facePaint.setColor(Color.GREEN);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(5f);
        noofPerson = firebaseVisionFaces.size();


        for(int i=0;i < firebaseVisionFaces.size();i++){

            canvas.drawRect(firebaseVisionFaces.get(i).getBoundingBox(),facePaint);

        }


    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {

        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        float rotationDgr = 0f;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    private void showProgress() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }
}