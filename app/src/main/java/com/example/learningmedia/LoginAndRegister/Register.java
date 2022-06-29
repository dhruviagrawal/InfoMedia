package com.example.learningmedia.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText fullname, password,Phoneno,email;
    Button register,login;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    public static final String SHARED_PREFS = "PREFS";
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
                String fullName = fullname.getText().toString().trim();
                String phoneNo = Phoneno.getText().toString().trim();
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

                // User is registered in the firebase
                SharedPreferences sharedPreferences =getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("emailId",emailn);
                editor.commit();
                progressBar.setVisibility(View.VISIBLE);
                // User is created with email and password
                fAuth.createUserWithEmailAndPassword(emailn, passwordn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            final String Uid = user.getUid();
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            // Add user data to Firebase
                            String userId = fAuth.getCurrentUser().getUid();
                            int len=emailn.length()-4;
                            final String userName = emailn.substring(0,len);
                            final HashMap<Object,String> hashMap=new HashMap<>();
                            hashMap.put("Name",fullName);
                            hashMap.put("Id",Uid);
                            hashMap.put("email",emailn);
                            hashMap.put("phone",phoneNo);
                            hashMap.put("imageurl","gs://learning-media-4cd52.appspot.com/user-png-icon-download-icons-logos-emojis-users-2240.png");
                            hashMap.put("bio"," ");
                            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Users");
                            dbReference.child(Uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            final DatabaseReference sharedPrefs = firebaseDatabase.getReference().child("Users").child(userName);
                            DatabaseReference interestRef = firebaseDatabase.getReference().child("Admin").child("Interest");
                            interestRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot i:snapshot.getChildren()){
                                        sharedPrefs.child(i.getKey()).setValue("0");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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