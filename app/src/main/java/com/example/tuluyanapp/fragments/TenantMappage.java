package com.example.tuluyanapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.tuluyanapp.R;

public class TenantMappage extends Fragment {

    private static final String TAG = "TenantMappage";

    private MapView mapView;
    private TextView addressBox;
    private LocationManager locationManager;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
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
                    Log.w(TAG, "Location permissions denied.");
                    addressBox.setText("Location permission denied.");
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OSM Configuration
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        // Initialize LocationManager
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_mappage, container, false);

        // Initialize UI components
        mapView = view.findViewById(R.id.osm_map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        addressBox = view.findViewById(R.id.address_box);
        addressBox.setText("Fetching location...");

        // Initialize Scan Button
        View scanButton = view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(v -> {
            // Logic for scanning the area
            Log.d(TAG, "Scan Area button clicked!");
        });

        // Request permissions for location
        requestPermissionsLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // Check permissions and request location updates
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());

            // Update the map to show the current location
            updateMapLocation(location.getLatitude(), location.getLongitude());
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

    private void updateMapLocation(double latitude, double longitude) {
        // Update the map view and marker with the current location
        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        mapView.getController().setZoom(18.0);
        mapView.getController().setCenter(currentLocation);

        // Add or update the marker
        Marker marker = new Marker(mapView);
        marker.setPosition(currentLocation);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("You are here");
        mapView.getOverlays().clear();
        mapView.getOverlays().add(marker);

        // Update the address box with the coordinates
        addressBox.setText("Lat: " + latitude + ", Lon: " + longitude);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
