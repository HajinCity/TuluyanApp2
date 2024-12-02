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
    private String tenantId = null;
    private String landlordId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_views_boarding_house);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            fetchTenantId(userId);
        } else {
            Log.e(TAG, "No logged-in user found");
        }

        String boardingHouseId = getIntent().getStringExtra("BOARDING_HOUSE_ID");
        String ownerNameText = getIntent().getStringExtra("ownerName");
        String paymentOptionText = getIntent().getStringExtra("paymentOption");

        apartmentName = findViewById(R.id.apartmentName);
        apartmentPrice = findViewById(R.id.apartmentPrice);
        descriptionDetails = findViewById(R.id.descriptionDetails);
        addressDetails = findViewById(R.id.addressDetails);
        distanceText = findViewById(R.id.distanceText);
        ownerName = findViewById(R.id.ownerName);
        paymentOption = findViewById(R.id.paymentOption);

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

        Button rentButton = findViewById(R.id.rentButton);
        rentButton.setOnClickListener(v -> {
            if (tenantId != null && landlordId != null) {
                Intent intent = new Intent(TenantViewsBoardingHouse.this, TenantRentNowBoardingHouse.class);
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
                                            landlordId = potentialLandlordId;

                                            String title = documentSnapshot.getString("title");
                                            String address = documentSnapshot.getString("address");
                                            String description = documentSnapshot.getString("description");
                                            Long price = documentSnapshot.getLong("price");

                                            latitude = documentSnapshot.getDouble("latitude");
                                            longitude = documentSnapshot.getDouble("longitude");

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
