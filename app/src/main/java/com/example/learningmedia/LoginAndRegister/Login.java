package com.example.learningmedia.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    EditText email1, password1;
    Button registerhere,login1;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth fAuth;
    ProgressBar progressBar1;
    SignInButton signInButton;
    CheckBox admin;
    String tag;
    private final static int RC_SIGN_IN=123;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email1 = findViewById(R.id.email1);
        password1 = findViewById((R.id.password1));
        registerhere = findViewById(R.id.registerhere);
        login1 = findViewById(R.id.login1);
        signInButton = findViewById(R.id.googlesignin);
        admin = findViewById(R.id.checkBox);

        //Google SignIn
        createRequest();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        fAuth = FirebaseAuth.getInstance();

        progressBar1 = findViewById(R.id.progressBar1);
        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailn = email1.getText().toString().trim();
                String passwordn = password1.getText().toString().trim();

                if (TextUtils.isEmpty(emailn)) {
                    email1.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty((passwordn))) {
                    password1.setError(("Password is Required"));
                    return;
                }
                if (passwordn.length() <= 6) {
                    password1.setError("Password should be greater than 6");
                    return;
                }
                progressBar1.setVisibility(View.VISIBLE);

                // Authentication of user
                fAuth.signInWithEmailAndPassword(emailn,passwordn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        }else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
    public void createRequest(){
        //Configure Google  Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google Sign In  was successful, authenticate with firebase
                GoogleSignInAccount account =task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch(ApiException e){
                //Google Sign In Failed
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Sign in success, update UI with signed-in user's information
                    FirebaseUser user=fAuth.getCurrentUser();
                    Intent intent = new Intent(getApplicationContext(),StartActivity.class);
                    startActivity(intent);
                }
                else{
                    //sign in fails
                    Toast.makeText(Login.this,"sign in failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}