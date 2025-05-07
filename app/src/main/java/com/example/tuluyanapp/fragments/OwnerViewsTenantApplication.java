package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.firestore.FirebaseFirestore;

import com.example.tuluyanapp.R;


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

        // Get current timestamp as string
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create the map for updates
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        // Add approvedDate or rejectedDate based on status
        if (status.equals("Approved")) {
            updates.put("approvedDate", currentDate);
        } else if (status.equals("Rejected")) {
            updates.put("rejectedDate", currentDate);
        }

        // Update in LandlordCollection
        db.collection("LandlordCollection").document(landlordId)
                .collection("BoardingHouses").document(boardingHouseId)
                .collection("Occupants").document(tenantId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    // Update in TenantCollection
                    db.collection("TenantCollection").document(tenantId)
                            .collection("RentedBoardingHouse").document(landlordId)
                            .update(updates)
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
