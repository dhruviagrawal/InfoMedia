package com.example.learningmedia.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.learningmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class StartActivity extends AppCompatActivity {
    public static final String TAG = "StartActivity";
    //Context
    private Context StrtContext = StartActivity.this;

    public static final String SHARED_PREFS = "sharedPrefs";
    BottomNavigationView bottomNavigationView;
    private Context mContext;
    final Fragment[] selectFragment = {null};
    //Fragment selectFragment = null;
    //Context Calling
    private static final int ActivityNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BottomNavigation();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);


        bottomNavigationView.setOnItemSelectedListener(item-> {
            //  public boolean onNavigationItemReselected (MenuItem item){
            switch (item.getItemId()) {
                //  declare all the icons of bottom navigation bar
                case R.id.ic_house:
                    selectFragment[0] = new Fragment();
                    break;
                case R.id.ic_profile:
                    // selectFragment[0] = new ProfileFragment();
                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
                    editor.putString("profilefield", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectFragment[0] = new ProfileFragment();
                    break;
                case R.id.ic_user:
                    selectFragment[0] = new UserFragment();
                    break;
                case R.id.ic_upload:
                    selectFragment[0]=null;
                    startActivity(new Intent(StartActivity.this, UploadActivity.class));
                    break;
            }
            if (selectFragment[0] != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment[0]).commit();
            }
            //return true;
            //}
            return true;
        } );
        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.ic_profile);
        }
        else
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}