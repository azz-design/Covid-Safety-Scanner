package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class user extends AppCompatActivity {
    private String value;
    private static final String MESSAGE_ID = "message_prefs";

    private Button menuButton;
    private TextView message;
    private Button signOut;
    private int count;
    // private int nopop;
    // private String value1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("NOP");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        signOut = findViewById(R.id.sign_out_user);
        menuButton = findViewById(R.id.button_to_menu);
        message = findViewById(R.id.noOfPerople);


        //if(getIntent().getStringExtra("noofpeople")!= null) {

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        int nop = 0;

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            People people = snapshot.toObject(People.class);

                            nop = people.getNoofpeople();


                        }
                        count = nop;

                        if (count == 0) {
                            Toast.makeText(user.this, "Empty Location", Toast.LENGTH_SHORT).show();
                            message.setText("No of People out there : " + count + "\nSafe Going Out There");
                        } else if (count > 4) {
                            Toast.makeText(user.this, "Not Safe Going Out\nCOVID ALERT!!!", Toast.LENGTH_SHORT).show();
                            message.setText("No of People out there : " + count + "\nNot Safe Going Out There\nCOVID ALERT!!!");
                        } else if (count > 0 && count <= 4) {
                            Toast.makeText(user.this, "Safe Going Out", Toast.LENGTH_SHORT).show();
                            message.setText("No of People out there : " + count + "\nSafe Going Out There");
                        }


                    }

                });







        // Log.d("AAFAT", "onSuccess: " + count);


//            SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
//            SharedPreferences.Editor editor =sharedPreferences.edit();
//            editor.putString("Message", String.valueOf(value));
//
//            editor.apply();


        // }
//        else
//        {
////            SharedPreferences getSharedData = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
////            value1 = getSharedData.getString("Message","No Data Input");
//
//            if(value1==null){
//                Toast.makeText(this, "Not yet Uploaded", Toast.LENGTH_SHORT).show();
//            }
//
//           // Toast.makeText(this, "Not yet Uploaded", Toast.LENGTH_SHORT).show();
//            message.setText("Past Data : \nNo of People out there : " + nopop + "\nUpload New Data ");
//        }


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(user.this, intro.class);
                startActivity(intent);
                finish();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(user.this, "SignOut Successful ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(user.this, LoginFragment.class);
                startActivity(intent);
                finish();
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();


            }
        });


    }
}



