package com.example.tuluyanapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton; // Import AppCompatButton

public class EditTenantProfileActivity extends AppCompatActivity {

    // Declare UI elements
    private ImageButton tEditProfileBack;
    private TextView textViewTitle;
    private EditText tFirstName, tMiddleName, tSurName, tAddress, tContactNo, tEmailAddress, tProfileAge, tBirthDate;
    private AppCompatButton cancelBtn, saveTenantProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile); // Reference to your XML file

        // Initialize UI elements
        tEditProfileBack = findViewById(R.id.tEditProfileBack);
        textViewTitle = findViewById(R.id.textViewTitle);
        tFirstName = findViewById(R.id.tFirstName);
        tMiddleName = findViewById(R.id.tMiddleName);
        tSurName = findViewById(R.id.tSurName);
        tAddress = findViewById(R.id.tAddress);
        tContactNo = findViewById(R.id.tContactNo);
        tEmailAddress = findViewById(R.id.tEmailAddress);
        tProfileAge = findViewById(R.id.tProfileAge);
        tBirthDate = findViewById(R.id.tBirthDate);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveTenantProfile = findViewById(R.id.saveTenantProfile);

        // Set up event listeners (if any)
        setUpListeners();
    }

    private void setUpListeners() {
        // Set back button behavior (this will finish the activity and go back to the previous one)
        tEditProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Close the activity (back to previous screen)
            }
        });

        // Set up save button (you can replace this with your logic to save the profile)
        saveTenantProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to save tenant profile (get values from EditText fields)
                String firstName = tFirstName.getText().toString();
                String middleName = tMiddleName.getText().toString();
                String surName = tSurName.getText().toString();
                String address = tAddress.getText().toString();
                String contactNo = tContactNo.getText().toString();
                String emailAddress = tEmailAddress.getText().toString();
                String profileAge = tProfileAge.getText().toString();
                String birthDate = tBirthDate.getText().toString();

                // You can add validation and saving logic here
                if (firstName.isEmpty() || surName.isEmpty() || address.isEmpty() || contactNo.isEmpty()) {
                    // Show an error message to the user
                    // e.g., Toast.makeText(EditTenantProfileActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Logic to save profile, e.g., save to a database or API call
                }
            }
        });

        // Set up cancel button (if needed)
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for cancel action (e.g., close activity or reset fields)
                finish();
            }
        });
    }
}
