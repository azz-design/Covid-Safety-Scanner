package com.example.covid1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class intro extends AppCompatActivity {
    private Button signOut;
    private Button administratorButton;
    private Button userButton;
    private Button abooutUs;
    private Button scanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        signOut = findViewById(R.id.sign_out);
        administratorButton = findViewById(R.id.administrator_button);
        userButton = findViewById(R.id.user_button);
        abooutUs = findViewById(R.id.about_us);
        scanning = findViewById(R.id.scanning_intro);


        scanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HardwareVam.class);
                startActivity(intent);
            }
        });

        abooutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(intro.this, "SignOut Successful ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),LoginFragment.class);
                startActivity(intent);
                SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                finish();

            }
        });

        administratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Scanner.class);
                startActivity(intent);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ListofData.class);
                startActivity(intent);
            }
        });
    }
}
