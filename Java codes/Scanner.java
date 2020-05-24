package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Date;

import java.util.List;

public class Scanner extends AppCompatActivity {
    private Button signOut;
    private static final String MESSAGE_ID = "message_prefs";

    private Button uploadImage;
    private Button submitImage;
    private ImageView imagePreview;
    private int noofPerson = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

//    private DocumentReference nopRef = db.collection("NOP")
//            .document("First Data");

    private CollectionReference collectionReference =  db.collection("NOP");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        signOut = findViewById(R.id.sign_out_scanner);
        uploadImage = findViewById(R.id.button_To_Upload);
        submitImage = findViewById(R.id.button_to_submit);
        imagePreview = findViewById(R.id.uploadImage);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(Scanner.this);
            }
        });





        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Scanner.this, "SignOut Successful ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Scanner.this,LoginFragment.class);
                startActivity(intent);
                SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                finish();

            }
        });

        submitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();


                Intent intent1 =new Intent(Scanner.this,ListofData.class);
               // intent1.putExtra("noofpeople",String.valueOf(noofPerson));
                startActivity(intent1);
                finish();


            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                assert result != null;
                Uri imageuri = result.getUri();
                try {
                    analyseImage(MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void analyseImage(final Bitmap bitmap) {

        if(bitmap == null){
            Toast.makeText(this, "There was an Error", Toast.LENGTH_SHORT).show();
            return;
        }
        imagePreview.setImageBitmap(bitmap);

        showProgress();

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
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
                        imagePreview.setImageBitmap(mutableImage);

                        hideProgress();




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Scanner.this, "There was an Error", Toast.LENGTH_SHORT).show();
                        hideProgress();



                    }
                });

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

    private void addData() {
        int nop = noofPerson;
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());


        People people = new People();
        people.setNoofpeople(nop);



        people.setTime(currentDate);


        collectionReference.add(people);
        collectionReference.orderBy(currentDate);
    }
    private void showProgress() {
        findViewById(R.id.scanner_progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        findViewById(R.id.scanner_progressBar).setVisibility(View.GONE);
    }
}
