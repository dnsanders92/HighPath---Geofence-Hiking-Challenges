package com.example.highpath.Fragments.LoginFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Database.DatabaseServices.CreateLocalUser;
import com.example.highpath.Database.Supabase.LoginUser;
import com.example.highpath.Database.Supabase.SyncActivities;
import com.example.highpath.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private static final String TAG = "SyncDebug";

    private EditText loginEmail, loginPassword;
    private Button loginButton, registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginEmail = view.findViewById(R.id.loginEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> performLogin());

        registerButton.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void performLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing In...");

        final Context appContext = requireContext().getApplicationContext();

        LoginUser.login(appContext, email, password, new LoginUser.LoginCallback() {
            @Override
            public void onSuccess(JSONObject userData) {

                // 1. Sync user profile to local Room DB
                CreateLocalUser.syncCloudToLocal(appContext, userData);

                try {
                    int userId = userData.getInt("user_id");

                    // 2. Sync activities
                    SyncActivities.fetchUserActivities(appContext, userId,
                            new SyncActivities.ActivitySyncCallback() {
                                @Override
                                public void onSuccess(JSONArray activities) {
                                    CreateLocalUser.syncActivitiesToLocal(appContext, activities);
                                    Log.d(TAG, "Activities synced on login: " + activities.length());
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e(TAG, "Activity sync failed: " + error);
                                }
                            });

                    // 3. Sync splits
                    SyncActivities.fetchUserSplits(appContext, userId,
                            new SyncActivities.SplitSyncCallback() {
                                @Override
                                public void onSuccess(JSONArray splits) {
                                    CreateLocalUser.syncSplitsToLocal(appContext, splits);
                                    Log.d(TAG, "Splits synced on login: " + splits.length());
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e(TAG, "Splits sync failed: " + error);
                                }
                            });

                } catch (Exception e) {
                    Log.e(TAG, "Could not get userId for sync: " + e.getMessage());
                }

                // 4. Navigate to MainActivity
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(appContext, MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("Sign In");
                        Toast.makeText(appContext, error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}