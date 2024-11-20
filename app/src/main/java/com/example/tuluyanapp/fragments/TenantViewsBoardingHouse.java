package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tuluyanapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TenantViewsBoardingHouse extends AppCompatActivity {

    private static final String TAG = "TenantViewsBoardingHouse";

    private TextView apartmentName, apartmentPrice, descriptionDetails, addressDetails, distanceText, ownerName, paymentOption;
    private ImageView topImage, ownerAvatar;
    private Double latitude = null;
    private Double longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_views_boarding_house);

        // Get the passed data
        String boardingHouseId = getIntent().getStringExtra("BOARDING_HOUSE_ID");
        String ownerNameText = getIntent().getStringExtra("ownerName");
        String paymentOptionText = getIntent().getStringExtra("paymentOption");
        String distanceTextValue = getIntent().getStringExtra("distance");

        // Reference UI elements
        apartmentName = findViewById(R.id.apartmentName);
        apartmentPrice = findViewById(R.id.apartmentPrice);
        descriptionDetails = findViewById(R.id.descriptionDetails);
        addressDetails = findViewById(R.id.addressDetails);
        distanceText = findViewById(R.id.distanceText);
        ownerName = findViewById(R.id.ownerName);
        paymentOption = findViewById(R.id.paymentOption);
        topImage = findViewById(R.id.topImage);
        ownerAvatar = findViewById(R.id.ownerAvatar);

        // Set values to UI
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

        // Set OnClickListener for "Get Directions" button
        Button mapViewButton = findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(v -> {
            Intent intent = new Intent(TenantViewsBoardingHouse.this, TenantGetDirections.class);

            // Pass latitude and longitude to the next activity
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);

            startActivity(intent);
        });
    }

    private void fetchBoardingHouseData(String boardingHouseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            db.collection("LandlordCollection")
                                    .document(landlordDoc.getId())
                                    .collection("BoardingHouses")
                                    .document(boardingHouseId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String title = documentSnapshot.getString("title");
                                            String address = documentSnapshot.getString("address");
                                            String description = documentSnapshot.getString("description");
                                            String imageUrl = documentSnapshot.getString("imageUrl");
                                            Long price = documentSnapshot.getLong("price");

                                            latitude = documentSnapshot.getDouble("latitude");
                                            longitude = documentSnapshot.getDouble("longitude");

                                            Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

                                            // Update UI
                                            apartmentName.setText(title);
                                            addressDetails.setText(address);
                                            descriptionDetails.setText(description);
                                            apartmentPrice.setText(getString(R.string.price_format, price != null ? price : 0));

                                            if (imageUrl != null) {
                                                Glide.with(this).load(imageUrl).into(topImage);
                                            }
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
