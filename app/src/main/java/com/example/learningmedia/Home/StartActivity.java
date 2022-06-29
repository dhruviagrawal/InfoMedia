package com.example.learningmedia.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.learningmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
                    //declare all the icons of bottom navigation bar
                    case R.id.ic_house:
                        selectFragment[0] = new HomeFragment();
                        break;
                    case R.id.ic_profile:
                        selectFragment[0] = new ProfileFragment();
                        break;
                    case R.id.ic_user:
                        selectFragment[0] = new UserFragment();
                        break;
                    case R.id.ic_upload:
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
                        editor.putString("profilefield", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        selectFragment[0] = new UploadFragment();
                        break;
                }
                if (selectFragment[0] != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment[0]).commit();
                }
                //return true;
            //}
            return true;
        } );
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}
