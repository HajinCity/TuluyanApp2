package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.MainActivity2;
import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.TenantRequestApplicationAdapter;
import com.example.tuluyanapp.models.TenantRequestModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerProfilepage extends Fragment {

    private static final String TAG = "OwnerProfilepage";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView usernameTextView;
    private RecyclerView requestsRecyclerView;
    private TenantRequestApplicationAdapter requestsAdapter;
    private List<TenantRequestModel> tenantRequestList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_profilepage, container, false);

        // Find views
        usernameTextView = view.findViewById(R.id.username);
        ImageView settingsIcon = view.findViewById(R.id.Owner_settings_icon);
        requestsRecyclerView = view.findViewById(R.id.requestsRecyclerView);

        // Set up RecyclerView
        tenantRequestList = new ArrayList<>();
        requestsAdapter = new TenantRequestApplicationAdapter(requireContext(), tenantRequestList);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsRecyclerView.setAdapter(requestsAdapter);

        // Fetch and display user data
        fetchOwnerName();
        fetchTenantRequests();

        // Set up settings icon click listener for popup menu
        settingsIcon.setOnClickListener(this::showPopupMenu);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tenantRequestList.clear(); // Clear the existing list
        requestsAdapter.notifyDataSetChanged(); // Clear the RecyclerView
        fetchTenantRequests();    // Fetch the updated tenant requests
    }

    private void fetchOwnerName() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("LandlordCollection").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("FirstName");
                        String lastName = documentSnapshot.getString("LastName");

                        if (firstName != null) {
                            String fullName = (lastName != null && !lastName.isEmpty()) ? firstName + " " + lastName : firstName;
                            usernameTextView.setText(fullName);
                        } else {
                            usernameTextView.setText("Unknown Name");
                        }
                    } else {
                        usernameTextView.setText("Unknown Name");
                        Log.w(TAG, "Owner document not found in Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching owner name", e);
                    Toast.makeText(getContext(), "Failed to fetch owner name", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchTenantRequests() {
        String landlordId = mAuth.getCurrentUser().getUid();

        // Clear the list before fetching data
        tenantRequestList.clear();
        requestsAdapter.notifyDataSetChanged(); // Immediately clear the RecyclerView

        db.collection("LandlordCollection").document(landlordId).collection("BoardingHouses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot boardingHouse : queryDocumentSnapshots) {
                        String boardingHouseId = boardingHouse.getId();
                        fetchOccupants(boardingHouseId, landlordId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching boarding houses", e);
                    Toast.makeText(getContext(), "Failed to fetch boarding houses", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchOccupants(String boardingHouseId, String landlordId) {
        db.collection("LandlordCollection").document(landlordId)
                .collection("BoardingHouses").document(boardingHouseId)
                .collection("Occupants")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot occupant : queryDocumentSnapshots) {
                        String id = occupant.getId();
                        String firstName = occupant.getString("firstName");
                        String lastName = occupant.getString("lastName");
                        String status = occupant.getString("status");

                        // Check for duplicates and status
                        if (status != null && status.equals("Pending") &&
                                tenantRequestList.stream().noneMatch(t -> t.getId().equals(id))) {
                            tenantRequestList.add(new TenantRequestModel(id, firstName, lastName, status, boardingHouseId, landlordId));
                        }
                    }
                    requestsAdapter.notifyDataSetChanged(); // Notify adapter about changes
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching occupants", e);
                    Toast.makeText(getContext(), "Failed to fetch occupants", Toast.LENGTH_SHORT).show();
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
}
