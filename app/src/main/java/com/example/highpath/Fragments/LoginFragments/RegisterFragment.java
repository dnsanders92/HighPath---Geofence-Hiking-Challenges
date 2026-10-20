package com.example.highpath.Fragments.LoginFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Database.DatabaseServices.CreateLocalUser;
import com.example.highpath.Database.Supabase.CreateUser;
import com.example.highpath.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private TextInputEditText registerEmail, registerPassword, confirmPassword, firstName, surname, birthYear;
    private AutoCompleteTextView birthMonth;
    private MaterialButton completeRegistration;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupMonthDropdown();
        completeRegistration.setOnClickListener(v -> submitRegistration());
    }

    private void bindViews(View view) {
        registerEmail        = view.findViewById(R.id.registerEmail);
        registerPassword     = view.findViewById(R.id.registerPassword);
        confirmPassword      = view.findViewById(R.id.confirmPassword);
        firstName            = view.findViewById(R.id.firstName);
        surname              = view.findViewById(R.id.surname);
        birthMonth           = view.findViewById(R.id.birthMonth);
        birthYear            = view.findViewById(R.id.birthYear);
        completeRegistration = view.findViewById(R.id.completeRegisterButton);
    }

    private void setupMonthDropdown() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, MONTHS);
        birthMonth.setAdapter(adapter);
    }

    private void submitRegistration() {
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String confirmPw = confirmPassword.getText().toString().trim();
        String fName = firstName.getText().toString().trim();
        String sName = surname.getText().toString().trim();
        String bMonth = birthMonth.getText().toString().trim();
        String bYearStr = birthYear.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all account credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPw)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer bYear = bYearStr.isEmpty() ? 0 : Integer.parseInt(bYearStr);

        completeRegistration.setEnabled(false);
        completeRegistration.setText("Registering...");

        CreateUser.signUp(
                requireContext(),
                email, password,
                fName, sName, bMonth, bYear,
                new CreateUser.UserCallback() {
                    @Override
                    public void onSuccess(long userId) {
                        // Save user locally — navigate only after confirmed written
                        CreateLocalUser.addUser(requireContext(), (int) userId, fName, sName, bMonth, bYear, () -> {
                            Log.d("RegisterFragment", "User saved locally — navigating to MainActivity");
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Intent intent = new Intent(requireContext(), MainActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("RegisterFragment", "error: " + error);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                completeRegistration.setEnabled(true);
                                completeRegistration.setText("Create Account");
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }
        );
    }
}