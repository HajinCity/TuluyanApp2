package com.example.tuluyanapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OwnerPostpage extends Fragment {

    private static final String TAG = "OwnerPostpage";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Location services
    private LocationManager locationManager;

    // UI Components
    private EditText titleEditText, priceEditText, slotsEditText, addressEditText, descriptionEditText, otherDetailsEditText;
    private Spinner paymentOptionsSpinner, selectionOptionsSpinner;
    private View postButton;

    public OwnerPostpage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_postpage, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize LocationManager
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Initialize UI components
        titleEditText = view.findViewById(R.id.board_title);
        priceEditText = view.findViewById(R.id.board_price);
        slotsEditText = view.findViewById(R.id.board_slots);
        addressEditText = view.findViewById(R.id.board_address);
        descriptionEditText = view.findViewById(R.id.board_description);
        otherDetailsEditText = view.findViewById(R.id.board_otherDetails);
        paymentOptionsSpinner = view.findViewById(R.id.board_paymentOptions);
        selectionOptionsSpinner = view.findViewById(R.id.board_selectionOptions);
        postButton = view.findViewById(R.id.postBoardingHouseBtn);

        // Set default text for address and fetch GPS-based address
        addressEditText.setText(getString(R.string.fetching_address));
        requestLocationPermission();

        // Set up button click listener
        postButton.setOnClickListener(v -> postBoardingHouse());

        // Set a listener to repopulate the address when cleared
        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && TextUtils.isEmpty(addressEditText.getText().toString())) {
                getLocation();
            }
        });

        return view;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            getAddressFromLocation(location);
        }
    };

    private void getAddressFromLocation(Location location) {
        if (!isAdded()) return;
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                if (addressEditText != null) {
                    addressEditText.post(() -> addressEditText.setText(addressText));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to get address from location", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required to fetch address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postBoardingHouse() {
        String userUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userUID == null) {
            Toast.makeText(getContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String slotsStr = slotsEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String otherDetails = otherDetailsEditText.getText().toString();
        String paymentOption = paymentOptionsSpinner.getSelectedItem().toString();
        String selectionOption = selectionOptionsSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(slotsStr) || TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int slots;
        try {
            price = Double.parseDouble(priceStr);
            slots = Integer.parseInt(slotsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price or slots input", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique boardingHouseId
        String boardingHouseId = db.collection("LandlordCollection")
                .document(userUID)
                .collection("BoardingHouses")
                .document()
                .getId();

        Map<String, Object> boardingHouse = new HashMap<>();
        boardingHouse.put("boardingHouseId", boardingHouseId); // Replace userUID with boardingHouseId
        boardingHouse.put("title", title);
        boardingHouse.put("price", price);
        boardingHouse.put("paymentOption", paymentOption);
        boardingHouse.put("slots", slots);
        boardingHouse.put("address", address);
        boardingHouse.put("description", description);
        boardingHouse.put("otherDetails", otherDetails);
        boardingHouse.put("selectionOption", selectionOption);

        db.collection("LandlordCollection")
                .document(userUID)
                .collection("BoardingHouses")
                .document(boardingHouseId) // Use boardingHouseId as the document ID
                .set(boardingHouse)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Boarding house posted successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding boarding house to nested collection", e);
                    Toast.makeText(getContext(), "Error posting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        titleEditText.setText("");
        priceEditText.setText("");
        slotsEditText.setText("");
        addressEditText.setText(getString(R.string.fetching_address)); // Reset to fetching address
        descriptionEditText.setText("");
        otherDetailsEditText.setText("");
        paymentOptionsSpinner.setSelection(0);
        selectionOptionsSpinner.setSelection(0);
        getLocation(); // Re-fetch GPS-based address
    }
}
