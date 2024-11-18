package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.List;

public class TenantHomepage extends Fragment {

    private static final String TAG = "TenantHomepage";

    private RecyclerView nearestBHRecyclerView;
    private RecyclerView newListingBHRecyclerView;
    private ProgressBar progressBarNearestBH;
    private ProgressBar progressBarNewListing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_homepage, container, false);

        // Reference UI components
        nearestBHRecyclerView = view.findViewById(R.id.nearestBH);
        newListingBHRecyclerView = view.findViewById(R.id.newListingBH);
        progressBarNearestBH = view.findViewById(R.id.progressBar9);
        progressBarNewListing = view.findViewById(R.id.progressBar10);

        // Set up RecyclerViews
        setupNearestBHRecyclerView();
        setupNewListingRecyclerView();

        return view;
    }

    private void setupNearestBHRecyclerView() {
        // Configure RecyclerView with a horizontal layout manager
        nearestBHRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Sample data for demonstration
        List<NearestBoardingH> sampleData = new ArrayList<>();
        sampleData.add(new NearestBoardingH("Parcon Apartment", "₱3000/month", "2kms away", "https://example.com/image1.jpg"));
        sampleData.add(new NearestBoardingH("Sunrise Inn", "₱3500/month", "3kms away", "https://example.com/image2.jpg"));
        sampleData.add(new NearestBoardingH("Cozy Stay", "₱2500/month", "1.5kms away", "https://example.com/image3.jpg"));

        // Set up the adapter
        TenantNearestBHAdapter adapter = new TenantNearestBHAdapter(getContext(), sampleData);
        nearestBHRecyclerView.setAdapter(adapter);

        // Hide progress bar after data is loaded
        progressBarNearestBH.setVisibility(View.GONE);
    }

    private void setupNewListingRecyclerView() {
        // Configure RecyclerView with a horizontal layout manager
        newListingBHRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Show progress bar while loading data
        progressBarNewListing.setVisibility(View.VISIBLE);

        // Fetch data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<ListingBHModels> newListData = new ArrayList<>();

                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            db.collection("LandlordCollection")
                                    .document(landlordDoc.getId())
                                    .collection("BoardingHouses")
                                    .get()
                                    .addOnCompleteListener(boardingHouseTask -> {
                                        if (boardingHouseTask.isSuccessful() && boardingHouseTask.getResult() != null) {
                                            for (QueryDocumentSnapshot boardingHouseDoc : boardingHouseTask.getResult()) {
                                                String title = boardingHouseDoc.getString("title");
                                                String price = String.valueOf(boardingHouseDoc.getLong("price"));
                                                String selectionOption = boardingHouseDoc.getString("selectionOption");
                                                String imageUrl = boardingHouseDoc.getString("imageUrl");

                                                // Add to the list
                                                newListData.add(new ListingBHModels(
                                                        title,
                                                        "PHP " + price + ".00, " + selectionOption,
                                                        imageUrl
                                                ));
                                            }

                                            // Set up the adapter after loading all data
                                            ListingBHAdapters newAdapter = new ListingBHAdapters(getContext(), newListData);
                                            newListingBHRecyclerView.setAdapter(newAdapter);

                                            // Hide progress bar after data is loaded
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

}
