package com.example.tuluyanapp.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.SearchResultAdapter;
import com.example.tuluyanapp.adapters.TenantSearchAdapter;
import com.example.tuluyanapp.models.TenantSearchedModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tenantsearchpage extends Fragment {

    private RecyclerView searchedItemsRecyclerView;
    private EditText searchEditText;
    private View progressBar;
    private TextView suggestionLabel;
    private TenantSearchAdapter defaultAdapter;
    private List<TenantSearchedModel> allSearchData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_searchpage, container, false);

        // Initialize UI components
        searchedItemsRecyclerView = view.findViewById(R.id.searchedItemsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditTextTenant);
        progressBar = view.findViewById(R.id.progressBar5);
        suggestionLabel = view.findViewById(R.id.suggestionLabel);

        // Set up RecyclerView
        searchedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchedItemsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        });

        // Set up search action
        searchEditText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchEditText.getText().toString().trim();
                searchFirestoreData(query); // Call the search method
                return true;
            }
            return false;
        });

        // Fetch and display default data
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
                        allSearchData.clear();
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
                                                String address = boardingHouseDoc.getString("address");
                                                int price = boardingHouseDoc.getLong("price").intValue();
                                                String selectionOption = boardingHouseDoc.getString("selectionOption");
                                                String boardingHouseId = boardingHouseDoc.getId();

                                                allSearchData.add(new TenantSearchedModel(
                                                        ownerName,
                                                        title,
                                                        price,
                                                        selectionOption,
                                                        boardingHouseId,
                                                        address
                                                ));
                                                // Debugging: Log each item added
                                                Log.d("AllData", "Added item: " + title + " | Address: " + address);
                                            }

                                            // Set default adapter with all data
                                            defaultAdapter = new TenantSearchAdapter(getContext(), allSearchData);
                                            searchedItemsRecyclerView.setAdapter(defaultAdapter);
                                            progressBar.setVisibility(View.GONE); // Hide ProgressBar
                                        } else {
                                            Log.e("FirestoreError", "Error fetching boarding houses: ", boardingHouseTask.getException());
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        Log.d("AllData", "Total items in allSearchData: " + allSearchData.size());
                    } else {
                        Log.e("FirestoreError", "Error fetching landlord documents: ", task.getException());
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data: ", e);
                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                });
    }

    private void searchFirestoreData(String query) {
        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar while searching
        List<TenantSearchedModel> filteredData = new ArrayList<>();

        // Filter results based on the address field
        for (TenantSearchedModel data : allSearchData) {
            Log.d("SearchDebug", "Checking address: " + data.getAddress());
            if (data.getAddress() != null && data.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredData.add(data);
            }
        }

        Log.d("SearchDebug", "Filtered items count: " + filteredData.size());
        for (TenantSearchedModel item : filteredData) {
            Log.d("SearchDebug", "Filtered item: " + item.getTitle());
        }

        // Handle search results
        if (filteredData.isEmpty()) {
            // Update TextView to show no results
            suggestionLabel.setText(String.format("No results found for \"%s\"", query));
            searchedItemsRecyclerView.setAdapter(new SearchResultAdapter(getContext(), new ArrayList<>())); // Clear RecyclerView
        } else {
            // Update TextView to show results
            suggestionLabel.setText(String.format("Showing %d results for \"%s\"", filteredData.size(), query));

            // Replace RecyclerView adapter with SearchResultAdapter
            SearchResultAdapter searchAdapter = new SearchResultAdapter(getContext(), filteredData);
            searchedItemsRecyclerView.setAdapter(searchAdapter);
        }

        progressBar.setVisibility(View.GONE); // Hide ProgressBar
    }
}
