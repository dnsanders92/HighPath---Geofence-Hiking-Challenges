package com.example.highpath.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.highpath.Fragments.LoginFragments.LoginFragment;
import com.example.highpath.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Loads login fragment onCreate.
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_container, new LoginFragment())
                    .commit();
        }
    }
}
