package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.TenantViewsApplicationsAdapter;
import com.example.tuluyanapp.models.TenantViewApplicationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TenantApplicationView extends AppCompatActivity {

    private RecyclerView reviewingRecyclerView, cancelledRecyclerView, rejectedRecyclerView, approvedRecyclerView;
    private TextView reviewingEmptyText, cancelledEmptyText, rejectedEmptyText, approvedEmptyText;
    private TenantViewsApplicationsAdapter reviewingAdapter, cancelledAdapter, rejectedAdapter, approvedAdapter;
    private List<TenantViewApplicationModel> reviewingList = new ArrayList<>();
    private List<TenantViewApplicationModel> cancelledList = new ArrayList<>();
    private List<TenantViewApplicationModel> rejectedList = new ArrayList<>();
    private List<TenantViewApplicationModel> approvedList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_application_view);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerViews and TextViews
        initRecyclerViews();

        // Set up Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Navigate back to the previous activity

        // Fetch Applications Data
        fetchApplications();
    }


    private void initRecyclerViews() {
        reviewingRecyclerView = findViewById(R.id.reviewingRecyclerView);
        cancelledRecyclerView = findViewById(R.id.cancelledRecyclerView);
        rejectedRecyclerView = findViewById(R.id.rejectedRecyclerView);
        approvedRecyclerView = findViewById(R.id.approvedRecyclerView);

        reviewingEmptyText = findViewById(R.id.reviewingEmptyText);
        cancelledEmptyText = findViewById(R.id.cancelledEmptyText);
        rejectedEmptyText = findViewById(R.id.rejectedEmptyText);
        approvedEmptyText = findViewById(R.id.approvedEmptyText);

        reviewingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelledRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rejectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewingAdapter = new TenantViewsApplicationsAdapter(reviewingList);
        cancelledAdapter = new TenantViewsApplicationsAdapter(cancelledList);
        rejectedAdapter = new TenantViewsApplicationsAdapter(rejectedList);
        approvedAdapter = new TenantViewsApplicationsAdapter(approvedList);

        reviewingRecyclerView.setAdapter(reviewingAdapter);
        cancelledRecyclerView.setAdapter(cancelledAdapter);
        rejectedRecyclerView.setAdapter(rejectedAdapter);
        approvedRecyclerView.setAdapter(approvedAdapter);
    }

    private void fetchApplications() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.e("FetchApplications", "User not logged in");
            return;
        }

        String tenantId = auth.getCurrentUser().getUid();

        db.collection("TenantCollection").document(tenantId)
                .collection("RentedBoardingHouse")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String price = "PHP " + document.getString("price");
                            String status = document.getString("status");

                            if (title != null && price != null && status != null) {
                                TenantViewApplicationModel application =
                                        new TenantViewApplicationModel(title, price, status);

                                // Add to corresponding list
                                addApplicationToList(status, application);
                            }
                        }
                        updateEmptyTexts();
                    } else {
                        Log.e("FetchApplications", "Error fetching applications", task.getException());
                    }
                });
    }

    private void addApplicationToList(String status, TenantViewApplicationModel application) {
        switch (status) {
            case "Pending":
                reviewingList.add(application);
                reviewingAdapter.notifyDataSetChanged();
                break;
            case "Cancelled":
                cancelledList.add(application);
                cancelledAdapter.notifyDataSetChanged();
                break;
            case "Rejected":
                rejectedList.add(application);
                rejectedAdapter.notifyDataSetChanged();
                break;
            case "Approved":
                approvedList.add(application);
                approvedAdapter.notifyDataSetChanged();
                break;
            default:
                Log.e("AddApplication", "Unknown status: " + status);
                break;
        }
    }

    private void updateEmptyTexts() {
        reviewingEmptyText.setVisibility(reviewingList.isEmpty() ? View.VISIBLE : View.GONE);
        reviewingRecyclerView.setVisibility(reviewingList.isEmpty() ? View.GONE : View.VISIBLE);

        cancelledEmptyText.setVisibility(cancelledList.isEmpty() ? View.VISIBLE : View.GONE);
        cancelledRecyclerView.setVisibility(cancelledList.isEmpty() ? View.GONE : View.VISIBLE);

        rejectedEmptyText.setVisibility(rejectedList.isEmpty() ? View.VISIBLE : View.GONE);
        rejectedRecyclerView.setVisibility(rejectedList.isEmpty() ? View.GONE : View.VISIBLE);

        approvedEmptyText.setVisibility(approvedList.isEmpty() ? View.VISIBLE : View.GONE);
        approvedRecyclerView.setVisibility(approvedList.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
