package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OwnerHomepage extends Fragment {

    private TextView textViewTotalListings;
    private TextView textViewFirstName;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_owner_homepage, container, false);

        // Initialize Firestore and TextViews
        db = FirebaseFirestore.getInstance();
        textViewTotalListings = view.findViewById(R.id.textViewTotalListings);
        textViewFirstName = view.findViewById(R.id.textView25);

        // Fetch and display total listings count
        fetchTotalListingsCount();

        // Fetch and display landlord's first name
        fetchLandlordFirstName();

        return view;
    }

    private void fetchTotalListingsCount() {
        // Get the currently logged-in landlord's UID
        String landlordUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (landlordUID == null) {
            Log.e("FirestoreError", "Landlord UID is null. Ensure the user is logged in.");
            textViewTotalListings.setText("Error");
            return;
        }

        // Fetch data from Firestore
        db.collection("LandlordCollection")
                .document(landlordUID)
                .collection("BoardingHouses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int totalListings = task.getResult().size(); // Count of boarding houses
                        textViewTotalListings.setText(String.valueOf(totalListings));
                        Log.d("FirestoreSuccess", "Total listings: " + totalListings);
                    } else {
                        textViewTotalListings.setText("0"); // Handle empty data
                        Log.d("FirestoreFailure", "Query succeeded but no data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    textViewTotalListings.setText("Error");
                    Log.e("FirestoreError", "Error fetching data", e);
                });
    }

    private void fetchLandlordFirstName() {
        // Get the currently logged-in landlord's UID
        String landlordUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (landlordUID == null) {
            Log.e("FirestoreError", "Landlord UID is null. Ensure the user is logged in.");
            textViewFirstName.setText("Error");
            return;
        }

        // Fetch the first name from Firestore
        db.collection("LandlordCollection")
                .document(landlordUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            String firstName = snapshot.getString("FirstName");
                            textViewFirstName.setText(firstName); // Set the FirstName to the TextView
                            Log.d("FirestoreSuccess", "First name: " + firstName);
                        } else {
                            textViewFirstName.setText("Unknown");
                            Log.d("FirestoreFailure", "Document does not exist.");
                        }
                    } else {
                        textViewFirstName.setText("Error");
                        Log.d("FirestoreFailure", "Error fetching document.");
                    }
                })
                .addOnFailureListener(e -> {
                    textViewFirstName.setText("Error");
                    Log.e("FirestoreError", "Error fetching data", e);
                });
    }
}
