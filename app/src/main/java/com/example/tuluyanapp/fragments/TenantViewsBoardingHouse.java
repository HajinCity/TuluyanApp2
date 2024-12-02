package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TenantViewsBoardingHouse extends AppCompatActivity {

    private static final String TAG = "TenantViewsBoardingHouse";

    private TextView apartmentName, apartmentPrice, descriptionDetails, addressDetails, distanceText, ownerName, paymentOption;
    private ImageView topImage, ownerAvatar;
    private Double latitude = null;
    private Double longitude = null;
    private String tenantId = null; // Variable to hold the tenantId
    private String landlordId = null; // Variable to hold the landlordId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_views_boarding_house);

        // Back Button Logic
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize Firebase Auth to get the logged-in user's UID
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            fetchTenantId(userId);
        } else {
            Log.e(TAG, "No logged-in user found");
        }

        // Other initializations...
        String boardingHouseId = getIntent().getStringExtra("BOARDING_HOUSE_ID");
        String ownerNameText = getIntent().getStringExtra("ownerName");
        String paymentOptionText = getIntent().getStringExtra("paymentOption");
        String distanceTextValue = getIntent().getStringExtra("distance");

        apartmentName = findViewById(R.id.apartmentName);
        apartmentPrice = findViewById(R.id.apartmentPrice);
        descriptionDetails = findViewById(R.id.descriptionDetails);
        addressDetails = findViewById(R.id.addressDetails);
        distanceText = findViewById(R.id.distanceText);
        ownerName = findViewById(R.id.ownerName);
        paymentOption = findViewById(R.id.paymentOption);
        topImage = findViewById(R.id.topImage); // Static image
        ownerAvatar = findViewById(R.id.ownerAvatar);

        if (distanceTextValue != null) {
            distanceText.setText(distanceTextValue);
        }

        if (paymentOptionText != null) {
            paymentOption.setText(paymentOptionText);
        }

        if (ownerNameText != null) {
            ownerName.setText(ownerNameText);
        }

        if (boardingHouseId != null) {
            fetchBoardingHouseData(boardingHouseId);
        } else {
            Log.e(TAG, "No Boarding House ID provided");
        }

        Button mapViewButton = findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(v -> {
            Intent intent = new Intent(TenantViewsBoardingHouse.this, TenantGetDirections.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });

        // Navigate to TenantRentNowBoardingHouse when the Rent button is clicked
        Button rentButton = findViewById(R.id.rentButton);
        rentButton.setOnClickListener(v -> {
            if (tenantId != null && landlordId != null) {
                Intent intent = new Intent(TenantViewsBoardingHouse.this, TenantRentNowBoardingHouse.class);
                // Pass the tenantId, landlordId, and boardingHouseId to the next activity
                intent.putExtra("TENANT_ID", tenantId);
                intent.putExtra("LANDLORD_ID", landlordId);
                intent.putExtra("BOARDING_HOUSE_ID", boardingHouseId);
                startActivity(intent);
            } else {
                Log.e(TAG, "Tenant ID or Landlord ID is null, cannot proceed");
            }
        });
    }

    private void fetchTenantId(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TenantCollection")
                .whereEqualTo("tenantId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        tenantId = document.getString("tenantId");
                        Log.d(TAG, "Fetched tenantId: " + tenantId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching tenantId", e));
    }

    private void fetchBoardingHouseData(String boardingHouseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            String potentialLandlordId = landlordDoc.getId();

                            db.collection("LandlordCollection")
                                    .document(potentialLandlordId)
                                    .collection("BoardingHouses")
                                    .document(boardingHouseId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            landlordId = potentialLandlordId; // Save the landlordId
                                            Log.d(TAG, "Fetched landlordId: " + landlordId);

                                            String title = documentSnapshot.getString("title");
                                            String address = documentSnapshot.getString("address");
                                            String description = documentSnapshot.getString("description");
                                            Long price = documentSnapshot.getLong("price");

                                            latitude = documentSnapshot.getDouble("latitude");
                                            longitude = documentSnapshot.getDouble("longitude");

                                            Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

                                            // Update UI
                                            apartmentName.setText(title);
                                            addressDetails.setText(address);
                                            descriptionDetails.setText(description);
                                            apartmentPrice.setText(getString(R.string.price_format, price != null ? price : 0));
                                        } else {
                                            Log.e(TAG, "Boarding house document not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching boarding house: ", e));
                        }
                    } else {
                        Log.e(TAG, "Error fetching landlord documents: ", task.getException());
                    }
                });
    }
}
