package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.example.tuluyanapp.MainActivity2;
import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class OwnerProfilepage extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView usernameTextView;
    private OwnerProfileClass ownerProfile;
    private ListenerRegistration listenerRegistration; // Declare ListenerRegistration

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize OwnerProfileClass
        ownerProfile = new OwnerProfileClass();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_profilepage, container, false);

        // Find views
        usernameTextView = view.findViewById(R.id.username);
        ImageView settingsIcon = view.findViewById(R.id.Owner_settings_icon);

        // Fetch and display user data
        fetchOwnerName(); // Call method to fetch name

        // Set up settings icon click listener for popup menu
        settingsIcon.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    private void fetchOwnerName() {
        String uid = mAuth.getCurrentUser().getUid();
        ownerProfile.setLandlordUID(uid);  // Set the landlord UID in OwnerProfileClass

        DocumentReference docRef = db.collection("LandlordCollection").document(uid);

        listenerRegistration = docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                // Log the error
                Log.e("OwnerProfilepage", "Error fetching data: " + error.getMessage());
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Log.d("OwnerProfilepage", "DocumentSnapshot data: " + documentSnapshot.getData());

                // Fetch the fields individually to ensure they are retrieved correctly
                String firstName = documentSnapshot.getString("FirstName");
                String lastName = documentSnapshot.getString("LastName");

                // Set the values in the ownerProfile object
                ownerProfile.setFirstName(firstName);
                ownerProfile.setLastName(lastName);

                // Log the retrieved names and UID for debugging
                Log.d("OwnerProfilepage", "First Name: " + firstName + ", Last Name: " + lastName);
                Log.d("OwnerProfilepage", "Landlord UID: " + ownerProfile.getLandlordUID());

                if (firstName != null) {
                    String fullName = (lastName != null && !lastName.isEmpty()) ? firstName + " " + lastName : firstName;
                    usernameTextView.setText(fullName);
                } else {
                    Log.d("OwnerProfilepage", "First Name is null or empty");
                    usernameTextView.setText("Unknown Name");
                }
            } else {
                Log.d("OwnerProfilepage", "Document does not exist or is empty");
                usernameTextView.setText("Unknown Name");
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onPopupMenuItemClick);
        popup.show();
    }

    private boolean onPopupMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_profile) {
            startActivity(new Intent(getActivity(), OwnerEditProfile.class));
            return true;
        } else if (id == R.id.settings) {
            startActivity(new Intent(getActivity(), OwnerSettings.class));
            return true;
        } else if (id == R.id.logout) {
            startActivity(new Intent(getActivity(), MainActivity2.class));
            requireActivity().finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove Firestore listener when fragment is destroyed
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
