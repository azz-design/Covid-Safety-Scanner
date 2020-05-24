package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterFragment extends AppCompatActivity {
    private EditText fullName;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText reEnterPassword;
    FirebaseAuth fAuth;
    FirebaseAuth fcheck;
    private Button registerButton;
    private CheckBox checkRemember;

    private TextView clickToLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullName = findViewById(R.id.edit_register_name);
        enterEmail = findViewById(R.id.edit_register_email);
        enterPassword = findViewById(R.id.edit_register_password);
        reEnterPassword = findViewById(R.id.edit_register_repassword);
        registerButton = findViewById(R.id.button_register);
        clickToLogin = findViewById(R.id.clickToLogin);
        checkRemember = findViewById(R.id.register_checkbox);


        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){

            Intent intent = new Intent(getApplicationContext(),intro.class);
            startActivity(intent);
            finish();

        }
        else if(checkbox.equals("false")){


        }

        fAuth = FirebaseAuth.getInstance();

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


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setFullName = fullName.getText().toString().trim();
                String setEmail = enterEmail.getText().toString().trim();
                String setPassword = enterPassword.getText().toString().trim();
                String setrePassword = reEnterPassword.getText().toString().trim();

                if(TextUtils.isEmpty(setFullName)){
                    fullName.setError("Name not Entered");
                    return;
                }


                if(TextUtils.isEmpty(setEmail)){
                    enterEmail.setError("Email not Entered");
                    return;
                }

                if(TextUtils.isEmpty(setPassword)){
                    enterPassword.setError("Password not Entered");
                    return;
                }
                if(TextUtils.isEmpty(setrePassword)){
                    reEnterPassword.setError("Please Re Enter Password");
                    return;
                }

                if(setPassword.length()<6){
                    enterPassword.setError("Password must be of more than 6 characters");
                return;
                }
                if(!setPassword.equals(setrePassword)){
                    reEnterPassword.setError("Enter Password Correctly");
                    return;
                }

                fAuth.fetchSignInMethodsForEmail(setEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {

                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        Boolean check = task.getResult().getSignInMethods().isEmpty();
                        //SignInMethodQueryResult check = task.getResult();
                        if(!check){
                            Toast.makeText(RegisterFragment.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                fAuth.createUserWithEmailAndPassword(setEmail,setPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterFragment.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),intro.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            Toast.makeText(RegisterFragment.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });
        clickToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginFragment .class);
                startActivity(intent);
                finish();

            }
        });
    }
}
