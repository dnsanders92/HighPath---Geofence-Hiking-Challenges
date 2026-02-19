package com.example.highpath.Fragments.LoginFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highpath.Activities.MainActivity;
import com.example.highpath.Database.DatabaseServices.AddUserAccount;
import com.example.highpath.R;

public class RegisterFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button completeRegistration = view.findViewById(R.id.completeRegisterButton);

        EditText firstName = view.findViewById(R.id.firstName);
        EditText surname = view.findViewById(R.id.surname);
        EditText birthMonth = view.findViewById(R.id.birthMonth);
        EditText birthYear = view.findViewById(R.id.birthYear);

        boolean userAdded = false;

        completeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getFirstName = firstName.getText().toString().trim();
                String getSurname = surname.getText().toString().trim();
                String getBirthMonth = birthMonth.getText().toString().trim();
                int getBirthYear = Integer.parseInt(birthYear.getText().toString().trim());

                AddUserAccount.addUser(
                        requireContext(),
                        getFirstName,
                        getSurname,
                        getBirthMonth,
                        getBirthYear
                );


                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}