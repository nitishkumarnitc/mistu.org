package com.example.nitish.mistuorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nitish.mistuorg.home.Home;
import com.example.nitish.mistuorg.login.GoogleLoginActivity;
import com.example.nitish.mistuorg.utils.Constants;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class Begin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_new);
        Firebase.setAndroidContext(this);

        if(Constants.isUserLogin(getApplicationContext())==true){
            Intent intent=new Intent(this,Home.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent=new Intent(this, GoogleLoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Constants.isUserLogin(getApplicationContext())==true){
            Intent intent=new Intent(this,Home.class);
            startActivity(intent);
            finish();
        }
    }
}

