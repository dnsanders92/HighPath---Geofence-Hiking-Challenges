package com.example.highpath.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.highpath.Database.DatabaseServices.CheckUserState;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.Navigation.NavigationControl;
import com.example.highpath.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckUserState.checkUser(this);

        BottomNavigationView navBar = findViewById(R.id.bottom_navigation);
        FragmentManager fragmentManager = getSupportFragmentManager();

        new NavigationControl(fragmentManager, navBar);

    }
}
