package com.example.learningmedia.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    EditText email1, password1;
    Button registerhere,login1;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth fAuth;
    ProgressBar progressBar1;
    SignInButton signInButton;
    CheckBox admin;
    String tag;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    Boolean checkBoxStatus;
    private final static int RC_SIGN_IN=123;
    public static final String SHARED_PREFS ="PREFS";

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

        fAuth = FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        progressBar1 = findViewById(R.id.progressBar1);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Google SignIn
        createRequest();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });
          if(user!=null){
              DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("admin");
              DatabaseReference databaseReference=dbReference.child("Id");
              //check if current user is admin, if current user is admin AdminActivity will open else startActivity will open
              databaseReference.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.hasChild(user.getUid())){
                          Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                          startActivity(intent);
                      }
                      else{
                          Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                          startActivity(intent);
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
          }
          //if the ckeckbox is checked login as admin else login as user
          checkBoxStatus= admin.isChecked();
          // Login is selected the check if the credential is valid or not
         // if it is valid then further it is checked it is admin login or user login
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
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Id");
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // If checkBox status is true i.e admin checkbox is checked then user will login as admin
                                    // then check its credibility if it is admin or not
                                    if(checkBoxStatus){
                                        if(snapshot.hasChild(firebaseUser.getUid())){
                                            Toast.makeText(Login.this, "Admin Login Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                                        }
                                        else{
                                            Toast.makeText(Login.this, "Not an Admin", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        if(snapshot.hasChild(firebaseUser.getUid())){

                                        }
                                        else{
                                            Toast.makeText(Login.this, "User Login Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        // if register button is clicked it will redirect it to register activity
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
                    user=fAuth.getCurrentUser();
                    // Once sign in is successful add user data to database
                    addUserToDatabase(user);
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

    private void addUserToDatabase(FirebaseUser user) {
        final GoogleSignInAccount googleSignInAccount= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(googleSignInAccount!=null){
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("emailId",googleSignInAccount.getEmail());
            editor.commit();
            final  DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
            final boolean[] val = {true};
            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild(user.getUid()))
                    {
                        val[0] =false;
                        final HashMap<Object,String>hashMap = new HashMap<>();
                        hashMap.put("Name",googleSignInAccount.getDisplayName());
                        hashMap.put("Id",user.getUid());
                        hashMap.put("email",googleSignInAccount.getEmail());
                        hashMap.put("phone"," ");
                        hashMap.put("imageurl"," ");
                        hashMap.put("bio"," ");
                        dbReference.child(user.getUid()).setValue(hashMap);
                        String email = googleSignInAccount.getEmail();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                        int len=email.length()-4;
                        final String userName = email.substring(0,len);

                        final DatabaseReference sharedPrefs = firebaseDatabase.getReference().child("Users").child(userName);
                        DatabaseReference interestRef = firebaseDatabase.getReference().child("Admin").child("Interest");
                        interestRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot i:snapshot.getChildren()){
                                    sharedPrefs.child(i.getKey()).setValue("0");
                                }
                                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else if(val[0]==true){
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}