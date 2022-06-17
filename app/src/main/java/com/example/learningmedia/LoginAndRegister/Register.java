package com.example.learningmedia.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    EditText fullname, password,Phoneno,email;
    Button register,login;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullname = findViewById(R.id.fullname);
        password = findViewById((R.id.password));
        Phoneno = findViewById(R.id.PhoneNo);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailn = email.getText().toString().trim();
                String passwordn = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailn)) {
                    email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty((passwordn))) {
                    password.setError(("Password is Required"));
                    return;
                }
                if (passwordn.length() <= 6) {
                    password.setError("Password should be greater than 6");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // User is registered in the firebase

                fAuth.createUserWithEmailAndPassword(emailn, passwordn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}