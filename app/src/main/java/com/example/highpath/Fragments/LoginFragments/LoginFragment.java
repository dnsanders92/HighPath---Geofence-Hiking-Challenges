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
import com.example.highpath.Database.Supabase.SyncTrailMapImages;
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

        // connect ui
        loginEmail = view.findViewById(R.id.loginEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        // login click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // go to register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.login_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void performLogin() {

        // get input
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // disable button
        loginButton.setEnabled(false);
        loginButton.setText("Signing In...");

        final Context appContext = requireContext().getApplicationContext();

        // login request
        LoginUser.login(appContext, email, password, new LoginUser.LoginCallback() {

            @Override
            public void onSuccess(JSONObject userData) {

                // save user locally
                CreateLocalUser.syncCloudToLocal(appContext, userData);

                try {
                    int userId = userData.getInt("user_id");

                    // fetch activities
                    SyncActivities.fetchUserActivities(appContext, userId,
                            new SyncActivities.ActivitySyncCallback() {

                                @Override
                                public void onSuccess(JSONArray activities) {
                                    Log.d(TAG, "activities fetched: " + activities.length());

                                    // write activities then sync images + splits
                                    CreateLocalUser.syncActivitiesToLocal(appContext, activities, () -> {

                                        Log.d(TAG, "activities written — syncing images + splits");

                                        // sync trail map images
                                        SyncTrailMapImages.sync(appContext, userId);

                                        // sync splits
                                        SyncActivities.fetchUserSplits(appContext, userId,
                                                new SyncActivities.SplitSyncCallback() {

                                                    @Override
                                                    public void onSuccess(JSONArray splits) {
                                                        CreateLocalUser.syncSplitsToLocal(appContext, splits);
                                                        Log.d(TAG, "splits synced: " + splits.length());
                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        Log.e(TAG, "splits sync failed: " + error);
                                                    }
                                                });
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e(TAG, "activity sync failed: " + error);
                                }
                            });

                    // sync completed challenge items
                    SyncActivities.fetchCompletedChallengeItems(appContext, userId,
                            new SyncActivities.ChallengeItemSyncCallback() {

                                @Override
                                public void onSuccess(JSONArray items) {
                                    CreateLocalUser.syncChallengeItemsToLocal(appContext, items);
                                    Log.d(TAG, "challenge items synced: " + items.length());
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e(TAG, "challenge items sync failed: " + error);
                                }
                            });

                } catch (Exception e) {
                    Log.e(TAG, "could not get userId: " + e.getMessage());
                }

                // go to main activity
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
