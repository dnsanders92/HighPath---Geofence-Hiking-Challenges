package com.example.highpath.Fragments.LoginFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Database.DatabaseServices.AddUserAccount;
import com.example.highpath.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    // Layouts
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout;
    private TextInputLayout firstNameLayout, surnameLayout, birthMonthLayout, birthYearLayout;

    // Fields
    private TextInputEditText registerEmail, registerPassword, confirmPassword;
    private TextInputEditText firstName, surname, birthYear;
    private AutoCompleteTextView birthMonth;

    // Strength indicator
    private View strengthBar1, strengthBar2, strengthBar3;
    private android.widget.TextView strengthLabel;
    private View strengthContainer;

    private MaterialButton completeRegistration;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupMonthDropdown();
        setupRealTimeValidation();
        setupPasswordStrengthMeter();

        completeRegistration.setOnClickListener(v -> {
            if (validateAll()) submitRegistration();
        });

        MaterialButton goToLogin = view.findViewById(R.id.goToLoginButton);
        if (goToLogin != null) {
            goToLogin.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }
    }

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------

    private void bindViews(View view) {
        emailLayout           = view.findViewById(R.id.emailLayout);
        passwordLayout        = view.findViewById(R.id.passwordLayout);
        confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);
        firstNameLayout       = view.findViewById(R.id.firstNameLayout);
        surnameLayout         = view.findViewById(R.id.surnameLayout);
        birthMonthLayout      = view.findViewById(R.id.birthMonthLayout);
        birthYearLayout       = view.findViewById(R.id.birthYearLayout);

        registerEmail    = view.findViewById(R.id.registerEmail);
        registerPassword = view.findViewById(R.id.registerPassword);
        confirmPassword  = view.findViewById(R.id.confirmPassword);
        firstName        = view.findViewById(R.id.firstName);
        surname          = view.findViewById(R.id.surname);
        birthMonth       = view.findViewById(R.id.birthMonth);
        birthYear        = view.findViewById(R.id.birthYear);

        strengthContainer = view.findViewById(R.id.passwordStrengthContainer);
        strengthBar1      = view.findViewById(R.id.strengthBar1);
        strengthBar2      = view.findViewById(R.id.strengthBar2);
        strengthBar3      = view.findViewById(R.id.strengthBar3);
        strengthLabel     = view.findViewById(R.id.strengthLabel);

        completeRegistration = view.findViewById(R.id.completeRegisterButton);
    }

    private void setupMonthDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                MONTHS
        );
        birthMonth.setAdapter(adapter);
        birthMonth.setOnItemClickListener((parent, v, position, id) ->
                birthMonthLayout.setError(null)
        );
    }

    private void setupRealTimeValidation() {
        addClearErrorWatcher(registerEmail,    emailLayout);
        addClearErrorWatcher(confirmPassword,  confirmPasswordLayout);
        addClearErrorWatcher(firstName,        firstNameLayout);
        addClearErrorWatcher(surname,          surnameLayout);
        addClearErrorWatcher(birthYear,        birthYearLayout);
    }

    private void setupPasswordStrengthMeter() {
        registerPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (passwordLayout.getError() != null) passwordLayout.setError(null);

                String pw = s.toString();
                if (pw.isEmpty()) {
                    strengthContainer.setVisibility(View.GONE);
                    return;
                }

                strengthContainer.setVisibility(View.VISIBLE);
                int score = scorePassword(pw);
                updateStrengthBar(score);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Password strength scoring
    // -------------------------------------------------------------------------

    /** Returns 1 (weak), 2 (fair), or 3 (strong) */
    private int scorePassword(String pw) {
        int score = 0;
        if (pw.length() >= 8)                        score++;
        if (pw.matches(".*[A-Z].*") &&
                pw.matches(".*[a-z].*"))                 score++;
        if (pw.matches(".*\\d.*") ||
                pw.matches(".*[^A-Za-z0-9].*"))          score++;
        return Math.max(score, 1);
    }

    private void updateStrengthBar(int score) {
        int colorWeak   = Color.parseColor("#D32F2F");  // red
        int colorFair   = Color.parseColor("#F9A825");  // amber
        int colorStrong = Color.parseColor("#0F6E56");  // app green
        int colorEmpty  = Color.parseColor("#E0E0E0");

        switch (score) {
            case 1:
                strengthBar1.setBackgroundColor(colorWeak);
                strengthBar2.setBackgroundColor(colorEmpty);
                strengthBar3.setBackgroundColor(colorEmpty);
                strengthLabel.setText("Weak");
                strengthLabel.setTextColor(colorWeak);
                break;
            case 2:
                strengthBar1.setBackgroundColor(colorFair);
                strengthBar2.setBackgroundColor(colorFair);
                strengthBar3.setBackgroundColor(colorEmpty);
                strengthLabel.setText("Fair");
                strengthLabel.setTextColor(colorFair);
                break;
            case 3:
            default:
                strengthBar1.setBackgroundColor(colorStrong);
                strengthBar2.setBackgroundColor(colorStrong);
                strengthBar3.setBackgroundColor(colorStrong);
                strengthLabel.setText("Strong");
                strengthLabel.setTextColor(colorStrong);
                break;
        }
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private boolean validateAll() {
        boolean valid = true;

        // --- Email ---
        String email = getStr(registerEmail);
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            valid = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            valid = false;
        }

        // --- Password ---
        String pw = getStr(registerPassword);
        if (pw.isEmpty()) {
            passwordLayout.setError("Password is required");
            valid = false;
        } else if (pw.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters");
            valid = false;
        } else if (scorePassword(pw) < 2) {
            passwordLayout.setError("Password is too weak — add uppercase or numbers");
            valid = false;
        }

        // --- Confirm password ---
        String cpw = getStr(confirmPassword);
        if (cpw.isEmpty()) {
            confirmPasswordLayout.setError("Please confirm your password");
            valid = false;
        } else if (!cpw.equals(pw)) {
            confirmPasswordLayout.setError("Passwords do not match");
            valid = false;
        }

        // --- First name ---
        String first = getStr(firstName);
        if (first.isEmpty()) {
            firstNameLayout.setError("First name is required");
            valid = false;
        } else if (!first.matches("[a-zA-Z '-]+")) {
            firstNameLayout.setError("Only letters, hyphens and apostrophes allowed");
            valid = false;
        }

        // --- Surname ---
        String sur = getStr(surname);
        if (sur.isEmpty()) {
            surnameLayout.setError("Surname is required");
            valid = false;
        } else if (!sur.matches("[a-zA-Z '-]+")) {
            surnameLayout.setError("Only letters, hyphens and apostrophes allowed");
            valid = false;
        }

        // --- Birth month ---
        if (birthMonth.getText().toString().trim().isEmpty()) {
            birthMonthLayout.setError("Please select a month");
            valid = false;
        }

        // --- Birth year ---
        String yearStr = getStr(birthYear);
        if (yearStr.isEmpty()) {
            birthYearLayout.setError("Birth year is required");
            valid = false;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year < 1900 || year > currentYear - 5) {
                    birthYearLayout.setError("Enter a valid birth year");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                birthYearLayout.setError("Year must be a number");
                valid = false;
            }
        }

        return valid;
    }

    // -------------------------------------------------------------------------
    // Submission
    // -------------------------------------------------------------------------

    private void submitRegistration() {
        completeRegistration.setEnabled(false);
        completeRegistration.setText("Creating account…");

        String getFirstName  = getStr(firstName);
        String getSurname    = getStr(surname);
        String getBirthMonth = birthMonth.getText().toString().trim();
        int    getBirthYear  = Integer.parseInt(getStr(birthYear));
        String getEmail      = getStr(registerEmail);
        String getPassword   = getStr(registerPassword);

        // Pass email + password to your AddUserAccount service when ready for cloud
        AddUserAccount.addUser(
                requireContext(),
                getFirstName,
                getSurname,
                getBirthMonth,
                getBirthYear
                // TODO: add getEmail, getPassword once cloud auth is wired up
        );

        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void addClearErrorWatcher(TextInputEditText field, TextInputLayout layout) {
        field.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (layout.getError() != null) layout.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private String getStr(TextInputEditText field) {
        Editable e = field.getText();
        return e != null ? e.toString().trim() : "";
    }
}