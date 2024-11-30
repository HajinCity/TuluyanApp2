package com.example.tuluyanapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuluyanapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TenantRentNowBoardingHouse extends AppCompatActivity {

    private static final String TAG = "TenantRentNowBoarding";

    private EditText firstNameEditText, lastNameEditText, mobileNumberEditText, emailAddressEditText, startDateEditText;
    private FirebaseFirestore db;
    private String landlordId;
    private String boardingHouseId;
    private String tenantId;

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize EditTexts
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        mobileNumberEditText = findViewById(R.id.mobileNumber);
        emailAddressEditText = findViewById(R.id.emailAddress);
        startDateEditText = findViewById(R.id.startDate);

        // Fetch Intent Extras
        landlordId = getIntent().getStringExtra("LANDLORD_ID");
        boardingHouseId = getIntent().getStringExtra("BOARDING_HOUSE_ID");
        tenantId = getIntent().getStringExtra("TENANT_ID");

        // Log and validate IDs
        Log.d(TAG, "Landlord ID: " + landlordId + ", Boarding House ID: " + boardingHouseId + ", Tenant ID: " + tenantId);
        if (landlordId == null || boardingHouseId == null || tenantId == null) {
            Log.e(TAG, "Landlord ID, Boarding House ID, or Tenant ID is null. Cannot proceed.");
            Toast.makeText(this, "Invalid data. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch and populate tenant details
        fetchTenantDetails();

        // Date Picker for Start Date
        startDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TenantRentNowBoardingHouse.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        startDateEditText.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Submit Rent Request
        Button rentButton = findViewById(R.id.rentButton);
        rentButton.setOnClickListener(v -> {
            if (validateInputs()) {
                addOccupantToFirestore();
            }
        });

        // Cancel Button
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fetchTenantDetails() {
        db.collection("TenantCollection").document(tenantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Tenant details: " + documentSnapshot.getData());

                        // Populate the fields
                        firstNameEditText.setText(documentSnapshot.getString("firstName"));
                        lastNameEditText.setText(documentSnapshot.getString("lastName"));
                        mobileNumberEditText.setText(documentSnapshot.getString("contactNo"));
                        emailAddressEditText.setText(documentSnapshot.getString("email"));
                    } else {
                        Log.e(TAG, "Tenant document does not exist.");
                        Toast.makeText(this, "Failed to fetch tenant details.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching tenant details: ", e);
                    Toast.makeText(this, "Error fetching tenant details.", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInputs() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String email = emailAddressEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() || startDate.isEmpty()) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addOccupantToFirestore() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String email = emailAddressEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();

        Map<String, Object> occupantData = new HashMap<>();
        occupantData.put("firstName", firstName);
        occupantData.put("lastName", lastName);
        occupantData.put("mobileNumber", mobileNumber);
        occupantData.put("email", email);
        occupantData.put("startDate", startDate);
        occupantData.put("status", "Pending");
        occupantData.put("boardingHouseId", boardingHouseId);
        occupantData.put("landlordId", landlordId);

        db.collection("LandlordCollection")
                .document(landlordId)
                .collection("BoardingHouses")
                .document(boardingHouseId)
                .collection("Occupants")
                .add(occupantData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding occupant: ", e);
                    Toast.makeText(this, "Error sending request. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
