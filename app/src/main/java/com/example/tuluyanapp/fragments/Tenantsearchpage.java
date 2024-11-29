package com.example.tuluyanapp.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.TenantSearchAdapter;
import com.example.tuluyanapp.models.TenantSearchedModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tenantsearchpage extends Fragment {

    private RecyclerView searchedItemsRecyclerView;
    private View progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_searchpage, container, false);

        // Initialize RecyclerView and ProgressBar
        searchedItemsRecyclerView = view.findViewById(R.id.searchedItemsRecyclerView);
        searchedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Add spacing between items directly with an inline ItemDecoration
        searchedItemsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
                outRect.top = spacing; // Top spacing
                outRect.bottom = spacing; // Bottom spacing
            }
        });

        progressBar = view.findViewById(R.id.progressBar5);

        // Fetch data from Firestore
        fetchFirestoreData();

        return view;
    }

    private void fetchFirestoreData() {
        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar while loading data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("LandlordCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<TenantSearchedModel> tenantSearchData = new ArrayList<>();

                        for (QueryDocumentSnapshot landlordDoc : task.getResult()) {
                            String firstName = landlordDoc.getString("FirstName");
                            String middleName = landlordDoc.getString("MiddleName");
                            String lastName = landlordDoc.getString("LastName");
                            String ownerName = firstName + " " + middleName + " " + lastName;

                            db.collection("LandlordCollection")
                                    .document(landlordDoc.getId())
                                    .collection("BoardingHouses")
                                    .get()
                                    .addOnCompleteListener(boardingHouseTask -> {
                                        if (boardingHouseTask.isSuccessful() && boardingHouseTask.getResult() != null) {
                                            for (QueryDocumentSnapshot boardingHouseDoc : boardingHouseTask.getResult()) {
                                                String title = boardingHouseDoc.getString("title");
                                                int price = boardingHouseDoc.getLong("price").intValue();
                                                String selectionOption = boardingHouseDoc.getString("selectionOption");
                                                String boardingHouseId = boardingHouseDoc.getId();

                                                tenantSearchData.add(new TenantSearchedModel(
                                                        ownerName,
                                                        title,
                                                        price,
                                                        selectionOption,
                                                        boardingHouseId
                                                ));
                                            }

                                            TenantSearchAdapter adapter = new TenantSearchAdapter(getContext(), tenantSearchData);
                                            adapter.setOnItemClickListener(tenant -> {
                                                // Intent to TenantViewsBoardingHouse
                                                Intent intent = new Intent(getContext(), TenantViewsBoardingHouse.class);
                                                intent.putExtra("BOARDING_HOUSE_ID", tenant.getBoardingHouseId());
                                                intent.putExtra("ownerName", tenant.getOwnerName());
                                                intent.putExtra("paymentOption", tenant.getSelectionOption());
                                                startActivity(intent);
                                            });

                                            searchedItemsRecyclerView.setAdapter(adapter);
                                            progressBar.setVisibility(View.GONE); // Hide ProgressBar
                                        } else {
                                            Log.e("Tenantsearchpage", "Error fetching boarding houses: ", boardingHouseTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("Tenantsearchpage", "Error fetching landlord documents: ", task.getException());
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Tenantsearchpage", "Error fetching data: ", e);
                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                });
    }

}
