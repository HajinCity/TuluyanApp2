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

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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
    private GeoPoint currentLocation;

    // UI Components
    private MapView mapView;
    private EditText addressEditText, titleEditText, priceEditText, descriptionEditText, otherDetailsEditText;
    private Spinner paymentOptionsSpinner, selectionOptionsSpinner;
    private View postButton;

    public OwnerPostpage() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_postpage, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Initialize UI components
        mapView = view.findViewById(R.id.osm_map_view);
        addressEditText = view.findViewById(R.id.board_address);
        titleEditText = view.findViewById(R.id.board_title);
        priceEditText = view.findViewById(R.id.board_price);
        descriptionEditText = view.findViewById(R.id.board_description);
        otherDetailsEditText = view.findViewById(R.id.board_otherDetails);
        paymentOptionsSpinner = view.findViewById(R.id.board_paymentOptions);
        selectionOptionsSpinner = view.findViewById(R.id.board_selectionOptions);
        postButton = view.findViewById(R.id.postBoardingHouseBtn);

        // Initialize MapView
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);
        } else {
            Log.e(TAG, "MapView is null during initialization.");
        }

        requestLocationPermission();

        // Set post button click listener
        postButton.setOnClickListener(v -> postBoardingHouse());

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
        } else {
            Log.e(TAG, "Location permission not granted.");
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            setMapMarker(currentLocation);
            getAddressFromLocation(location);
        }
    };

    private void setMapMarker(GeoPoint location) {
        if (mapView == null) {
            Log.e(TAG, "MapView is null, cannot set marker.");
            return;
        }

        mapView.getOverlays().clear(); // Clear existing markers
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Current Location");
        mapView.getOverlays().add(marker);
        mapView.getController().setZoom(16.0);
        mapView.getController().setCenter(location);
    }

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

    private void postBoardingHouse() {
        String userUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userUID == null) {
            Toast.makeText(getContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String otherDetails = otherDetailsEditText.getText().toString();
        String paymentOption = paymentOptionsSpinner.getSelectedItem().toString();
        String selectionOption = selectionOptionsSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price input", Toast.LENGTH_SHORT).show();
            return;
        }

        String boardingHouseId = db.collection("LandlordCollection")
                .document(userUID)
                .collection("BoardingHouses")
                .document()
                .getId();

        Map<String, Object> boardingHouse = new HashMap<>();
        boardingHouse.put("boardingHouseId", boardingHouseId);
        boardingHouse.put("title", title);
        boardingHouse.put("price", price);
        boardingHouse.put("paymentOption", paymentOption);
        boardingHouse.put("address", address);
        boardingHouse.put("latitude", currentLocation != null ? currentLocation.getLatitude() : null);
        boardingHouse.put("longitude", currentLocation != null ? currentLocation.getLongitude() : null);
        boardingHouse.put("description", description);
        boardingHouse.put("otherDetails", otherDetails);
        boardingHouse.put("selectionOption", selectionOption);

        db.collection("LandlordCollection")
                .document(userUID)
                .collection("BoardingHouses")
                .document(boardingHouseId)
                .set(boardingHouse)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Boarding house posted successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding boarding house", e);
                    Toast.makeText(getContext(), "Error posting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        titleEditText.setText("");
        priceEditText.setText("");
        addressEditText.setText("");
        descriptionEditText.setText("");
        otherDetailsEditText.setText("");
        paymentOptionsSpinner.setSelection(0);
        selectionOptionsSpinner.setSelection(0);
        getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
    }
}
