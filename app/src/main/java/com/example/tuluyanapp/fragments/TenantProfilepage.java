package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuluyanapp.R;

public class TenantProfilepage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_profile, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up Help button
        View helpButton = view.findViewById(R.id.help);
        if (helpButton != null) {
            helpButton.setOnClickListener(v -> {
                Intent helpIntent = new Intent(getActivity(), TenantHelp.class);
                startActivity(helpIntent);
            });
        }

        // Set up Settings button
        View settingsButton = view.findViewById(R.id.settings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent settingsIntent = new Intent(getActivity(), TenantSettings.class);
                startActivity(settingsIntent);
            });
        }

        // Set up Edit Profile button
        View editProfileButton = view.findViewById(R.id.tEditProfile);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> {
                Intent editProfileIntent = new Intent(getActivity(), TenantEditProfile.class);
                startActivity(editProfileIntent);
            });
        }

        return view;
    }
}
