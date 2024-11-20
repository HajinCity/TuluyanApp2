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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.ListingBHAdapters;
import com.example.tuluyanapp.adapters.TenantNearestBHAdapter;
import com.example.tuluyanapp.models.ListingBHModels;
import com.example.tuluyanapp.models.NearestBoardingH;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TenantHomepage extends Fragment {

    private static final String TAG = "TenantHomepage";

    private TextView addressTextView;
    private RecyclerView nearestBHRecyclerView;
    private RecyclerView newListingBHRecyclerView;
    private ProgressBar progressBarNearestBH;
    private ProgressBar progressBarNewListing;
    private LocationManager locationManager;

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Boolean fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    Log.d(TAG, "Fine location permission granted.");
                    startLocationUpdates();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    Log.d(TAG, "Coarse location permission granted.");
                    startLocationUpdates();
                } else {
                    Log.w(TAG, "Location permission denied.");
                    addressTextView.setText(R.string.permission_denied_message);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_homepage, container, false);

        // Reference UI components
        addressTextView = view.findViewById(R.id.textView24);
        nearestBHRecyclerView = view.findViewById(R.id.nearestBH);
        newListingBHRecyclerView = view.findViewById(R.id.newListingBH);
        progressBarNearestBH = view.findViewById(R.id.progressBar9);
        progressBarNewListing = view.findViewById(R.id.progressBar10);

        // Set up RecyclerViews
        setupNearestBHRecyclerView();
        setupNewListingRecyclerView();

        // Request location permissions
        requestMultiplePermissionsLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        return view;
    }

    private void setupNearestBHRecyclerView() {
        nearestBHRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupNewListingRecyclerView() {
        newListingBHRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressBarNewListing.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<ListingBHModels> newListData = new ArrayList<>();

                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            String firstName = landlordDoc.getString("FirstName");
                            String lastName = landlordDoc.getString("LastName");
                            String ownerName = firstName + " " + lastName; // Combine FirstName and LastName

                            db.collection("LandlordCollection")
                                    .document(landlordDoc.getId())
                                    .collection("BoardingHouses")
                                    .get()
                                    .addOnCompleteListener(boardingHouseTask -> {
                                        if (boardingHouseTask.isSuccessful() && boardingHouseTask.getResult() != null) {
                                            for (QueryDocumentSnapshot boardingHouseDoc : boardingHouseTask.getResult()) {
                                                String boardingHouseId = boardingHouseDoc.getId();
                                                String title = boardingHouseDoc.getString("title");
                                                String price = "₱" + boardingHouseDoc.getLong("price") + "/month";
                                                String imageUrl = boardingHouseDoc.getString("imageUrl");
                                                String paymentOption = boardingHouseDoc.getString("paymentOption");
                                                Double latitude = boardingHouseDoc.getDouble("latitude");
                                                Double longitude = boardingHouseDoc.getDouble("longitude");

                                                String distance = "Unknown";

                                                // Get the user's current location
                                                Location userLocation = null;
                                                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                    userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                                }

                                                // Calculate distance only if current location and target coordinates are available
                                                if (userLocation != null && latitude != null && longitude != null) {
                                                    Location targetLocation = new Location("Target");
                                                    targetLocation.setLatitude(latitude);
                                                    targetLocation.setLongitude(longitude);

                                                    float distanceInMeters = userLocation.distanceTo(targetLocation);
                                                    float distanceInKm = distanceInMeters / 1000; // Convert meters to kilometers
                                                    distance = String.format(Locale.getDefault(), "%.1f km away", distanceInKm);
                                                }

                                                // Add data to the list
                                                newListData.add(new ListingBHModels(
                                                        boardingHouseId, // Boarding house ID
                                                        title, // Boarding house title
                                                        price, // Price
                                                        imageUrl, // Image URL
                                                        ownerName, // Owner name
                                                        paymentOption, // Payment option
                                                        distance // Distance
                                                ));
                                            }

                                            // Set adapter
                                            ListingBHAdapters newAdapter = new ListingBHAdapters(getContext(), newListData);
                                            newListingBHRecyclerView.setAdapter(newAdapter);
                                            progressBarNewListing.setVisibility(View.GONE);
                                        } else {
                                            Log.e(TAG, "Error fetching boarding houses: ", boardingHouseTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Error fetching landlord documents: ", task.getException());
                        progressBarNewListing.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: ", e);
                    progressBarNewListing.setVisibility(View.GONE);
                });
    }




    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
            updateAddress(location);
            fetchAndDisplayNearestBoardingHouses(location);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.d(TAG, "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.d(TAG, "Provider disabled: " + provider);
        }
    };

    private void updateAddress(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                Log.d(TAG, "Address: " + address);
                addressTextView.post(() -> addressTextView.setText(address));
            } else {
                Log.w(TAG, "No address found for location");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch address", e);
        }
    }

    private void fetchAndDisplayNearestBoardingHouses(Location userLocation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<NearestBoardingH> nearestBHList = new ArrayList<>();

        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            String firstName = landlordDoc.getString("FirstName");
                            String lastName = landlordDoc.getString("LastName");
                            String ownerName = firstName + " " + lastName; // Combine FirstName and LastName

                            db.collection("LandlordCollection")
                                    .document(landlordDoc.getId())
                                    .collection("BoardingHouses")
                                    .get()
                                    .addOnCompleteListener(boardingHouseTask -> {
                                        if (boardingHouseTask.isSuccessful() && boardingHouseTask.getResult() != null) {
                                            for (QueryDocumentSnapshot boardingHouseDoc : boardingHouseTask.getResult()) {
                                                String boardingHouseId = boardingHouseDoc.getId();
                                                String title = boardingHouseDoc.getString("title");
                                                String price = "₱" + boardingHouseDoc.getLong("price") + "/month";
                                                Double latitude = boardingHouseDoc.getDouble("latitude");
                                                Double longitude = boardingHouseDoc.getDouble("longitude");
                                                String imageUrl = boardingHouseDoc.getString("imageUrl");
                                                String paymentOption = boardingHouseDoc.getString("paymentOption");

                                                if (latitude != null && longitude != null) {
                                                    float[] results = new float[1];
                                                    Location.distanceBetween(
                                                            userLocation.getLatitude(),
                                                            userLocation.getLongitude(),
                                                            latitude,
                                                            longitude,
                                                            results);
                                                    float distanceInKm = results[0] / 1000;

                                                    if (distanceInKm <= 3.0) {
                                                        nearestBHList.add(new NearestBoardingH(
                                                                boardingHouseId,
                                                                title,
                                                                price,
                                                                String.format(Locale.getDefault(), "%.1f km away", distanceInKm),
                                                                imageUrl,
                                                                ownerName,
                                                                paymentOption
                                                        ));
                                                    }
                                                } else {
                                                    Log.w(TAG, "Latitude or Longitude is null for boarding house: " + title);
                                                }
                                            }

                                            TenantNearestBHAdapter adapter = new TenantNearestBHAdapter(getContext(), nearestBHList);
                                            nearestBHRecyclerView.setAdapter(adapter);
                                            progressBarNearestBH.setVisibility(View.GONE);
                                        } else {
                                            Log.e(TAG, "Error fetching boarding houses: ", boardingHouseTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Error fetching landlord documents: ", task.getException());
                        progressBarNearestBH.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: ", e);
                    progressBarNearestBH.setVisibility(View.GONE);
                });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
