package com.example.learningmedia.Home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class User extends AppCompatActivity {
    public static final String TAG="User";
    //Context
    private Context UContext=User.this;
    //Context Calling
    private static final int ActivityNum =2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigation();
    }
    private void BottomNavigation(){
        Log.d(TAG,"Setting Bottom Navigation");
        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationHelper.BottomNavigationhlp(bottomNavigationView);
        BottomNavigationHelper.enableNavigation(UContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ActivityNum);
        menuItem.setChecked(true);
    }
}
