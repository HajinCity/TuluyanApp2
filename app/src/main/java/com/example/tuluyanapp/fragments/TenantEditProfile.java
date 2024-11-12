package com.example.tuluyanapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

        // Restrict tProfileAge to allow only two digits
        tProfileAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the length exceeds 2 digits
                if (s.length() > 2) {
                    tProfileAge.setText(s.subSequence(0, 2));
                    tProfileAge.setSelection(2); // Move cursor to the end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add TextWatcher to enforce single uppercase character in tMiddleName
        tMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    tMiddleName.setText(s.subSequence(0, 1));
                    tMiddleName.setSelection(1);
                } else if (s.length() == 1) {
                    String capitalized = s.toString().toUpperCase();
                    if (!s.toString().equals(capitalized)) {
                        tMiddleName.setText(capitalized);
                        tMiddleName.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up click listener for tBirthDate to open DatePickerDialog
        tBirthDate.setOnClickListener(v -> showDatePickerDialog());

        fetchUserData();

        // Setup save button with validation
        findViewById(R.id.saveTenantProfile).setOnClickListener(v -> saveUserData());

        // Set up cancel button to return to TenantProfilePage
        AppCompatButton cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());

        // Set up back button to return to TenantProfilePage
        ImageButton backBtn = findViewById(R.id.tEditProfileBack);
        backBtn.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tBirthDate.setText(date);
                },
                year, month, day
        );

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
        // Validate that all required fields are not empty
        if (tFirstName.getText().toString().trim().isEmpty() ||
                tMiddleName.getText().toString().trim().isEmpty() ||
                tSurName.getText().toString().trim().isEmpty() ||
                tAddress.getText().toString().trim().isEmpty() ||
                tContactNo.getText().toString().trim().isEmpty() ||
                tEmailAddress.getText().toString().trim().isEmpty() ||
                tProfileAge.getText().toString().trim().isEmpty() ||
                tBirthDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return;
        }

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
                userId
        );

        db.collection("TenantCollection").document(userId)
                .set(updatedProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
