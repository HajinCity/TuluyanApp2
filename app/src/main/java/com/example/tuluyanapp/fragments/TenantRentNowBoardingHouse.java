package com.example.tuluyanapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TenantRentNowBoardingHouse extends AppCompatActivity {

    private static final String TAG = "TenantRentNowBoarding";

    private EditText firstNameEditText, lastNameEditText, mobileNumberEditText, emailAddressEditText, startDateEditText;
    private RadioGroup roomPreferenceGroup;
    private Button inPersonButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String landlordId, boardingHouseId, tenantId, paymentMethod = "In-person";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_rent_now_boarding_house);

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        mobileNumberEditText = findViewById(R.id.mobileNumber);
        emailAddressEditText = findViewById(R.id.emailAddress);
        startDateEditText = findViewById(R.id.startDate);
        roomPreferenceGroup = findViewById(R.id.roomPreferenceGroup);
        inPersonButton = findViewById(R.id.inPerson);

        // Fetch data from Intent
        landlordId = getIntent().getStringExtra("LANDLORD_ID");
        boardingHouseId = getIntent().getStringExtra("BOARDING_HOUSE_ID");
        tenantId = getIntent().getStringExtra("TENANT_ID");

        // Validate incoming data
        if (landlordId == null || boardingHouseId == null || tenantId == null) {
            Toast.makeText(this, "Invalid data received.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup date picker for start date
        startDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        startDateEditText.setText(formattedDate);
                    },
                    year, month, day
            ).show();
        });

        // Handle in-person button toggle
        inPersonButton.setOnClickListener(v -> {
            paymentMethod = "In-person";
            inPersonButton.setBackgroundResource(R.color.blue1); // Change to blue
            inPersonButton.setTextColor(getResources().getColor(R.color.white)); // Set text to white
        });

        // Fetch tenant details if tenant ID is valid
        fetchTenantDetails();

        // Submit Button
        Button rentButton = findViewById(R.id.rentButton);
        rentButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitTenantRequest();
            }
        });
    }

    private void fetchTenantDetails() {
        db.collection("TenantCollection").document(tenantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String contactNo = documentSnapshot.getString("contactNo");
                        String email = documentSnapshot.getString("email");

                        // Populate fields
                        firstNameEditText.setText(firstName);
                        lastNameEditText.setText(lastName);
                        mobileNumberEditText.setText(contactNo);
                        emailAddressEditText.setText(email);
                    } else {
                        Log.w(TAG, "Tenant details not found for ID: " + tenantId);
                        Toast.makeText(this, "Failed to fetch tenant details.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching tenant details", e);
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
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (roomPreferenceGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a room preference.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitTenantRequest() {
        if (landlordId == null || boardingHouseId == null || tenantId == null) {
            Toast.makeText(this, "Invalid landlord or boarding house data", Toast.LENGTH_SHORT).show();
            return;
        }

        String occupantId = tenantId; // Use tenantId as occupantId for simplicity

        int selectedRoomPreferenceId = roomPreferenceGroup.getCheckedRadioButtonId();
        RadioButton selectedRoomPreference = findViewById(selectedRoomPreferenceId);
        String roomPreference = selectedRoomPreference.getText().toString();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("firstName", firstNameEditText.getText().toString().trim());
        requestData.put("lastName", lastNameEditText.getText().toString().trim());
        requestData.put("contactNo", mobileNumberEditText.getText().toString().trim());
        requestData.put("email", emailAddressEditText.getText().toString().trim());
        requestData.put("startDate", startDateEditText.getText().toString().trim());
        requestData.put("roomPreference", roomPreference);
        requestData.put("paymentMethod", paymentMethod);
        requestData.put("tenantId", tenantId); // Original tenant ID for reference
        requestData.put("status", "Pending");

        db.collection("LandlordCollection").document(landlordId)
                .collection("BoardingHouses").document(boardingHouseId)
                .collection("Occupants").document(occupantId)
                .set(requestData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Request submitted successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error submitting request", e);
                    Toast.makeText(this, "Error submitting request. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
