package com.example.learningmedia.Home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.learningmedia.Home.Profile;
import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.Home.Upload;
import com.example.learningmedia.Home.User;
import com.example.learningmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {
    private static final String TAG="BottomNavigationHelper";
    public static void BottomNavigationhlp(BottomNavigationView bottomNavigationView){
        Log.d(TAG,"Setting Bottom Navigation");
    }
    public static void enableNavigation(final Context context,BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                         //declare all the icons of bottom navigation bar
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, StartActivity.class);  //ActivityNum = 0
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_profile:
                        Intent intent2 = new Intent(context, Profile.class);   //ActivityNum = 1
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_user:
                        Intent intent3 = new Intent(context, User.class); // ActivityNum = 2
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_upload:
                        Intent intent4 = new Intent(context, Upload.class);  // ActivityNum = 3
                        context.startActivity(intent4);
                        break;
                }
                return false;
            }
        });
    }
}
