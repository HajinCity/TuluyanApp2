package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.tuluyanapp.MainActivity4;
import com.example.tuluyanapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditBoardingHouseActivity extends AppCompatActivity {

    private EditText titleField, priceField, slotsField, addressField, descriptionField, otherDetailsField;
    private AppCompatButton submitButton;
    private FirebaseFirestore db;
    private String landlordId;
    private String boardingHouseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_boarding_house);

        // Initialize views
        titleField = findViewById(R.id.EditBoard_title);
        priceField = findViewById(R.id.EditBoard_price);
        slotsField = findViewById(R.id.board_slots);
        addressField = findViewById(R.id.board_address);
        descriptionField = findViewById(R.id.board_description);
        otherDetailsField = findViewById(R.id.board_otherDetails);
        submitButton = findViewById(R.id.postBoardingHouseBtn);

        db = FirebaseFirestore.getInstance();

        // Fetch Intent extras
        landlordId = getIntent().getStringExtra("landlordId");
        boardingHouseId = getIntent().getStringExtra("boardingHouseId");

        Log.d("EditBoardingHouseActivity", "landlordId: " + landlordId + ", boardingHouseId: " + boardingHouseId);

        if (landlordId != null && boardingHouseId != null) {
            populateFields();
        } else {
            Toast.makeText(this, "Missing landlordId or boardingHouseId.", Toast.LENGTH_SHORT).show();
        }

        submitButton.setOnClickListener(v -> updateBoardingHouse());
    }

    private void populateFields() {
        DocumentReference docRef = db.collection("LandlordCollection")
                .document(landlordId)
                .collection("BoardingHouses")
                .document(boardingHouseId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                titleField.setText(documentSnapshot.getString("title"));
                if (documentSnapshot.contains("price")) {
                    priceField.setText(String.valueOf(documentSnapshot.getDouble("price")));
                }
                if (documentSnapshot.contains("slots")) {
                    slotsField.setText(String.valueOf(documentSnapshot.getLong("slots")));
                }
                addressField.setText(documentSnapshot.getString("address"));
                descriptionField.setText(documentSnapshot.getString("description"));
                otherDetailsField.setText(documentSnapshot.getString("otherDetails"));
            } else {
                Toast.makeText(this, "No data found for the given boarding house.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("FirestoreError", "Error fetching document", e);
            Toast.makeText(this, "Failed to load boarding house data.", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateBoardingHouse() {
        DocumentReference docRef = db.collection("LandlordCollection")
                .document(landlordId)
                .collection("BoardingHouses")
                .document(boardingHouseId);

        try {
            docRef.update(
                    "title", titleField.getText().toString(),
                    "price", Double.parseDouble(priceField.getText().toString()),
                    "slots", Integer.parseInt(slotsField.getText().toString()),
                    "address", addressField.getText().toString(),
                    "description", descriptionField.getText().toString(),
                    "otherDetails", otherDetailsField.getText().toString()
            ).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity4.class);
                intent.putExtra("fragmentToLoad", "OwnerHomepage");
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                Log.e("FirestoreError", "Error updating document", e);
                Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input. Please check your data.", Toast.LENGTH_SHORT).show();
            Log.e("InputError", "Invalid input", e);
        }
    }
}
