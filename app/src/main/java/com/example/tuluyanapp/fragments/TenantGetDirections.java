package com.example.tuluyanapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tuluyanapp.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TenantGetDirections extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "TenantGetDirections";
    private static final String GRAPHHOPPER_API_KEY = "938b3113-7e26-4d5a-9c70-4808ace8832f";

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private GeoPoint userLocation;
    private GeoPoint destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_tenant_get_directions);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        // Initialize location overlay
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        // Get destination coordinates from Intent
        double destLat = getIntent().getDoubleExtra("latitude", 0);
        double destLon = getIntent().getDoubleExtra("longitude", 0);
        destination = new GeoPoint(destLat, destLon);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchCurrentLocationAndDrawRoute();
        }
    }

    private void fetchCurrentLocationAndDrawRoute() {
        locationOverlay.runOnFirstFix(() -> {
            userLocation = locationOverlay.getMyLocation();
            if (userLocation != null) {
                runOnUiThread(() -> {
                    mapView.getController().setCenter(userLocation);
                    mapView.getController().setZoom(16.0); // Set initial zoom level
                });
                fetchRouteFromGraphhopper(userLocation, destination);
            } else {
                Log.e(TAG, "Unable to fetch user's current location.");
            }
        });
    }

    private void fetchRouteFromGraphhopper(GeoPoint start, GeoPoint end) {
        String url = String.format("https://graphhopper.com/api/1/route?point=%s,%s&point=%s,%s&vehicle=foot&locale=en&key=%s&type=json&points_encoded=false",
                start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), GRAPHHOPPER_API_KEY);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching route: ", e);
                runOnUiThread(() -> Toast.makeText(TenantGetDirections.this, "Failed to fetch route", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray paths = jsonResponse.getJSONArray("paths");
                        if (paths.length() > 0) {
                            JSONObject path = paths.getJSONObject(0);
                            JSONArray coordinates = path.getJSONObject("points").getJSONArray("coordinates");

                            List<GeoPoint> geoPoints = new ArrayList<>();
                            for (int i = 0; i < coordinates.length(); i++) {
                                JSONArray point = coordinates.getJSONArray(i);
                                double lon = point.getDouble(0);
                                double lat = point.getDouble(1);
                                geoPoints.add(new GeoPoint(lat, lon));
                            }

                            double distance = path.getDouble("distance") / 1000; // Distance in km
                            int timeInSeconds = path.getInt("time") / 1000; // Time in seconds

                            runOnUiThread(() -> {
                                drawRouteOnMap(geoPoints);
                                updateBottomPanel(distance, timeInSeconds);
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(TenantGetDirections.this, "No route found", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing route response: ", e);
                    }
                } else {
                    Log.e(TAG, "Route API response failed: " + response.message());
                }
            }
        });
    }

    private void drawRouteOnMap(List<GeoPoint> geoPoints) {
        Polyline routeLine = new Polyline();
        routeLine.setPoints(geoPoints);
        routeLine.setWidth(8f);
        routeLine.setColor(getResources().getColor(R.color.routeColor));
        mapView.getOverlays().add(routeLine);

        // Adjust map to fit route
        if (!geoPoints.isEmpty()) {
            GeoPoint startPoint = geoPoints.get(0);
            GeoPoint endPoint = geoPoints.get(geoPoints.size() - 1);

            mapView.zoomToBoundingBox(
                    new BoundingBox(
                            Math.max(startPoint.getLatitude(), endPoint.getLatitude()),
                            Math.max(startPoint.getLongitude(), endPoint.getLongitude()),
                            Math.min(startPoint.getLatitude(), endPoint.getLatitude()),
                            Math.min(startPoint.getLongitude(), endPoint.getLongitude())
                    ), true);

            mapView.invalidate(); // Refresh the map to show the updated view
        }
    }

    private void updateBottomPanel(double distance, int timeInSeconds) {
        String durationText = String.format("%d min", timeInSeconds / 60);
        String distanceText = String.format("%.1f km", distance);

        runOnUiThread(() -> {
            TextView durationTextView = findViewById(R.id.durationText);
            TextView distanceTextView = findViewById(R.id.distanceText);

            durationTextView.setText("Duration: " + durationText);
            distanceTextView.setText("Distance: " + distanceText);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocationAndDrawRoute();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
