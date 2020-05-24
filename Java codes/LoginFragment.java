package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends AppCompatActivity {
    private TextView clickToRegister;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private CheckBox checkRemember;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        clickToRegister = findViewById(R.id.clickToRegister);
        loginEmail = findViewById(R.id.edit_email);
        loginPassword = findViewById(R.id.edit_password);
        loginButton = findViewById(R.id.button_login);
        fAuth = FirebaseAuth.getInstance();
        checkRemember = findViewById(R.id.remember_me);
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){

            Intent intent = new Intent(getApplicationContext(),intro.class);
            startActivity(intent);
            finish();

        }
        else if(checkbox.equals("false")){


        }

        clickToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterFragment.class);
                startActivity(intent);
                finish();

            }
        });

        checkRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(compoundButton.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();

                }else if(!compoundButton.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();

                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setEmail = loginEmail.getText().toString().trim();
                String setPassword = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(setEmail)){
                    loginEmail.setError("Email not Entered");
                    return;
                }

                if(TextUtils.isEmpty(setPassword)){
                    loginPassword.setError("Password not Entered");
                    return;
                }


                if(setPassword.length()<6){
                    loginPassword.setError("Password must be of more than 6 characters");
                    return;
                }

                fAuth.signInWithEmailAndPassword(setEmail,setPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginFragment.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),intro.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginFragment.this, "Invalid Username and Password ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}
