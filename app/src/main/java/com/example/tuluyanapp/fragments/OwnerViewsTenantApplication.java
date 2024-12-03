package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuluyanapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class OwnerViewsTenantApplication extends AppCompatActivity {

    private static final String TAG = "OwnerViewsTenantApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_views_tenant_application);

        // Initialize UI elements
        ImageView backButton = findViewById(R.id.backButton);
        Button approveButton = findViewById(R.id.approveButton);
        Button rejectButton = findViewById(R.id.rejectButton);
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView mobileNumberTextView = findViewById(R.id.mobileNumberTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView roomPreferenceTextView = findViewById(R.id.roomPreferenceTextView);
        TextView startDateTextView = findViewById(R.id.startDateTextView);
        TextView paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        TextView requestSentDateTextView = findViewById(R.id.requestSentDateTextView);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Fetch IDs from intent
        String landlordId = getIntent().getStringExtra("landlordId");
        String boardingHouseId = getIntent().getStringExtra("boardingHouseId");
        String occupantId = getIntent().getStringExtra("occupantId");

        // Log intent data for debugging
        Log.d(TAG, "landlordId: " + landlordId + ", boardingHouseId: " + boardingHouseId + ", occupantId: " + occupantId);

        // Validate IDs
        if (landlordId == null || boardingHouseId == null || occupantId == null) {
            Toast.makeText(this, "Invalid IDs passed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch occupant data
        fetchTenantRequestData(
                landlordId,
                boardingHouseId,
                occupantId,
                nameTextView,
                mobileNumberTextView,
                emailTextView,
                roomPreferenceTextView,
                startDateTextView,
                paymentMethodTextView,
                requestSentDateTextView
        );

        // Approve button functionality
        approveButton.setOnClickListener(v -> updateRequestStatus(landlordId, boardingHouseId, occupantId, "Approved"));

        // Reject button functionality
        rejectButton.setOnClickListener(v -> updateRequestStatus(landlordId, boardingHouseId, occupantId, "Rejected"));
    }

    private void fetchTenantRequestData(
            String landlordId,
            String boardingHouseId,
            String occupantId,
            TextView nameTextView,
            TextView mobileNumberTextView,
            TextView emailTextView,
            TextView roomPreferenceTextView,
            TextView startDateTextView,
            TextView paymentMethodTextView,
            TextView requestSentDateTextView
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("LandlordCollection").document(landlordId)
                .collection("BoardingHouses").document(boardingHouseId)
                .collection("Occupants").document(occupantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Fetched document: " + documentSnapshot.getData());

                        // Fetch and display occupant data
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String contactNo = documentSnapshot.getString("contactNo");
                        String email = documentSnapshot.getString("email");
                        String startDate = documentSnapshot.getString("startDate");
                        String roomPreference = documentSnapshot.getString("roomPreference");
                        String paymentMethod = documentSnapshot.getString("paymentMethod");
                        String requestSentDate = documentSnapshot.getString("requestSentDate");

                        nameTextView.setText("Name: " + firstName + " " + lastName);
                        mobileNumberTextView.setText("Mobile Number: " + contactNo);
                        emailTextView.setText("Email Address: " + email);
                        roomPreferenceTextView.setText("Room Preference: " + (roomPreference != null ? roomPreference : "Not provided"));
                        startDateTextView.setText("Start Date: " + startDate);
                        paymentMethodTextView.setText("Payment Method: " + (paymentMethod != null ? paymentMethod : "Not provided"));
                        requestSentDateTextView.setText("Request Sent: " + (requestSentDate != null ? requestSentDate : "Not provided"));
                    } else {
                        Log.e(TAG, "Occupant not found for ID: " + occupantId);
                        Toast.makeText(this, "Request not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching request data: " + e.getMessage());
                    Toast.makeText(this, "Error fetching request data", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateRequestStatus(String landlordId, String boardingHouseId, String tenantId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update status in LandlordCollection
        db.collection("LandlordCollection").document(landlordId)
                .collection("BoardingHouses").document(boardingHouseId)
                .collection("Occupants").document(tenantId) // Updated to use tenantId
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    // Also update status in TenantCollection
                    db.collection("TenantCollection").document(tenantId)
                            .collection("RentedBoardingHouse").document(landlordId) // Updated to match new structure
                            .update("status", status)
                            .addOnSuccessListener(tenantUpdateUnused -> {
                                Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(tenantUpdateError -> {
                                Log.e(TAG, "Failed to update TenantCollection: " + tenantUpdateError.getMessage());
                                Toast.makeText(this, "Failed to update TenantCollection.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update LandlordCollection: " + e.getMessage());
                    Toast.makeText(this, "Failed to update LandlordCollection.", Toast.LENGTH_SHORT).show();
                });
    }

}
