package com.example.tuluyanapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuluyanapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class TenantRentNowBoardingHouse extends AppCompatActivity {

    private static final String TAG = "TenantRentNowBoarding";

    private EditText firstNameEditText, lastNameEditText, mobileNumberEditText, emailAddressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenant_rent_now_boarding_house);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize EditTexts
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        mobileNumberEditText = findViewById(R.id.mobileNumber);
        emailAddressEditText = findViewById(R.id.emailAddress);

        // Initialize RadioGroup and Button
        RadioGroup roomPreferenceGroup = findViewById(R.id.roomPreferenceGroup);
        Button inPersonButton = findViewById(R.id.inPerson);

        // Fetch Tenant ID from Intent
        String tenantId = getIntent().getStringExtra("TENANT_ID");
        Log.d(TAG, "Received tenantId: " + tenantId);

        // Fetch Tenant Data
        if (tenantId != null) {
            fetchTenantData(tenantId);
        } else {
            Log.e(TAG, "No Tenant ID provided");
        }

        // Date picker functionality
        EditText startDateEditText = findViewById(R.id.startDate);
        startDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TenantRentNowBoardingHouse.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date as MM/DD/YYYY
                        String formattedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        startDateEditText.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Toggle button functionality for inPerson
        inPersonButton.setOnClickListener(v -> {
            boolean isSelected = inPersonButton.isSelected();
            inPersonButton.setSelected(!isSelected);
        });

        // Handle Rent button click
        Button rentButton = findViewById(R.id.rentButton);
        rentButton.setOnClickListener(v -> {
            if (validateInputs(roomPreferenceGroup, inPersonButton, startDateEditText)) {
                // Show success message
                showSuccessMessage();
            } else {
                // Inform the user of missing fields
                Log.e(TAG, "Validation failed: Ensure all fields are filled and selections are made");
            }
        });

        // Handle Cancel button
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fetchTenantData(String tenantId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("TenantCollection").document(tenantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String contactNo = documentSnapshot.getString("contactNo");
                        String email = documentSnapshot.getString("email");

                        // Populate the EditText fields
                        firstNameEditText.setText(firstName);
                        lastNameEditText.setText(lastName);
                        mobileNumberEditText.setText(contactNo);
                        emailAddressEditText.setText(email);
                    } else {
                        Log.e(TAG, "No tenant data found for tenantId: " + tenantId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching tenant data", e));
    }

    private boolean validateInputs(RadioGroup roomPreferenceGroup, Button inPersonButton, EditText startDateEditText) {
        // Check if personal details are filled
        if (firstNameEditText.getText().toString().trim().isEmpty()) {
            firstNameEditText.setError("First Name is required");
            return false;
        }
        if (lastNameEditText.getText().toString().trim().isEmpty()) {
            lastNameEditText.setError("Last Name is required");
            return false;
        }
        if (mobileNumberEditText.getText().toString().trim().isEmpty()) {
            mobileNumberEditText.setError("Mobile Number is required");
            return false;
        }
        if (emailAddressEditText.getText().toString().trim().isEmpty()) {
            emailAddressEditText.setError("Email Address is required");
            return false;
        }

        // Check if a room preference is selected
        if (roomPreferenceGroup.getCheckedRadioButtonId() == -1) {
            Log.e(TAG, "No room preference selected");
            return false;
        }

        // Check if start date is selected
        if (startDateEditText.getText().toString().trim().isEmpty()) {
            startDateEditText.setError("Start Date is required");
            return false;
        }

        // Check if in-person payment method is selected
        if (!inPersonButton.isSelected()) {
            Log.e(TAG, "In-person payment method not selected");
            return false;
        }

        // All validations passed
        return true;
    }

    private void showSuccessMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_success)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    // Optionally finish the activity
                    finish();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
