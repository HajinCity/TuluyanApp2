package com.example.tuluyanapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.adapters.BoardingHouseAdapter;
import com.example.tuluyanapp.models.BoardingHouse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerManagepostpage extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyMessage;
    private BoardingHouseAdapter adapter;
    private List<BoardingHouse> boardingHouseList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_managepostpage, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyMessage = view.findViewById(R.id.emptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boardingHouseList = new ArrayList<>();
        adapter = new BoardingHouseAdapter(getContext(), boardingHouseList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchBoardingHouses();

        return view;
    }

    private void fetchBoardingHouses() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.GONE);

        String landlordUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference landlordRef = db.collection("LandlordCollection")
                .document(landlordUID)
                .collection("BoardingHouses");

        landlordRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful() && task.getResult() != null) {
                boardingHouseList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    BoardingHouse boardingHouse = document.toObject(BoardingHouse.class);
                    boardingHouse.setId(document.getId()); // Ensure document ID is set
                    boardingHouseList.add(boardingHouse);
                }

                if (boardingHouseList.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            } else {
                emptyMessage.setText("Failed to load data.");
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}
