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
import com.example.tuluyanapp.Settings;  // Import the Settings activity
import com.example.tuluyanapp.help;     // Import the Help activity
import com.example.tuluyanapp.EditTenantProfileActivity;  // Import the Edit Tenant Profile Activity

public class profilepage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.new_profile, container, false);

        // Handle window insets for layout (padding for system bars like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the Help button and set an OnClickListener to navigate to the Help activity
        View helpButton = view.findViewById(R.id.help);
        if (helpButton != null) {
            helpButton.setOnClickListener(v -> {
                Intent helpIntent = new Intent(getActivity(), help.class); // Navigate to Help Activity
                startActivity(helpIntent);
            });
        }

        // Find the Settings button and set an OnClickListener to navigate to the Settings activity
        View settingsButton = view.findViewById(R.id.settings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent settingsIntent = new Intent(getActivity(), Settings.class); // Navigate to Settings Activity
                startActivity(settingsIntent);
            });
        }

        // Find the "Edit" button and set an OnClickListener to navigate to EditTenantProfileActivity
        View editButton = view.findViewById(R.id.button29);
        if (editButton != null) {
            editButton.setOnClickListener(v -> {
                // Create an Intent to start the EditTenantProfileActivity
                Intent editIntent = new Intent(getActivity(), EditTenantProfileActivity.class);
                startActivity(editIntent); // Start the EditTenantProfileActivity
            });
        }

        return view; // Return the inflated view
    }
}
