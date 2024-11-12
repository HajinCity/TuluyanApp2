package com.example.tuluyanapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class TenantEditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText tFirstName, tMiddleName, tSurName, tAddress, tContactNo, tEmailAddress, tProfileAge, tBirthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenant_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tFirstName = findViewById(R.id.tFirstName);
        tMiddleName = findViewById(R.id.tMiddleName);
        tSurName = findViewById(R.id.tSurName);
        tAddress = findViewById(R.id.tAddress);
        tContactNo = findViewById(R.id.tContactNo);
        tEmailAddress = findViewById(R.id.tEmailAddress);
        tProfileAge = findViewById(R.id.tProfileAge);
        tBirthDate = findViewById(R.id.tBirthDate);

        // Set up click listener for tBirthDate to open DatePickerDialog
        tBirthDate.setOnClickListener(v -> showDatePickerDialog());

        fetchUserData();
        findViewById(R.id.saveTenantProfile).setOnClickListener(v -> saveUserData());
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date and set it on the EditText
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tBirthDate.setText(date);
                },
                year, month, day
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void fetchUserData() {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("TenantCollection").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        TenantProfileClass profile = documentSnapshot.toObject(TenantProfileClass.class);
                        if (profile != null) {
                            Log.d("TenantEditProfile", "User data fetched: " + profile.getFirstName());
                            populateFields(profile);
                        } else {
                            Log.d("TenantEditProfile", "Profile data is null");
                        }
                    } else {
                        Log.d("TenantEditProfile", "No data found for user");
                        Toast.makeText(this, "No data found for user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TenantEditProfile", "Error loading profile", e);
                    Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void populateFields(TenantProfileClass profile) {
        tFirstName.setText(profile.getFirstName());
        tMiddleName.setText(profile.getMiddleName());
        tSurName.setText(profile.getLastName());
        tAddress.setText(profile.getAddress());
        tContactNo.setText(profile.getContactNo());
        tEmailAddress.setText(profile.getEmail());
        tProfileAge.setText(profile.getAge());
        tBirthDate.setText(profile.getBirthdate());
    }

    private void saveUserData() {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        TenantProfileClass updatedProfile = new TenantProfileClass(
                tFirstName.getText().toString().trim(),
                tMiddleName.getText().toString().trim(),
                tSurName.getText().toString().trim(),
                tAddress.getText().toString().trim(),
                tContactNo.getText().toString().trim(),
                tEmailAddress.getText().toString().trim(),
                tProfileAge.getText().toString().trim(),
                tBirthDate.getText().toString().trim(),
                userId // Passing tenantId here
        );

        db.collection("TenantCollection").document(userId)
                .set(updatedProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
