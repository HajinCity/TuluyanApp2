package com.example.tuluyanapp.fragments;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class TenantProfilepage extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration listenerRegistration;
    private TextView nameTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_profile, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameTextView = view.findViewById(R.id.textView31);

        fetchUserName();

        // Set up button listeners (Help, Settings, Edit Profile)
        setUpButtons(view);

        return view;
    }

    private void fetchUserName() {
        String uid = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("TenantCollection").document(uid);

        listenerRegistration = docRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                // Handle the error
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Convert document snapshot to TenantProfileClass
                TenantProfileClass profile = documentSnapshot.toObject(TenantProfileClass.class);
                if (profile != null) {
                    String firstName = profile.getFirstName();
                    String lastName = profile.getLastName();
                    String fullName = (lastName != null && !lastName.isEmpty()) ? firstName + " " + lastName : firstName;

                    nameTextView.setText(fullName != null ? fullName : "Unknown Name");
                } else {
                    nameTextView.setText("Unknown Name");
                }
            } else {
                nameTextView.setText("Unknown Name");
            }
        });
    }


    private void setUpButtons(View view) {
        // Set up Help button
        View helpButton = view.findViewById(R.id.help);
        if (helpButton != null) {
            helpButton.setOnClickListener(v -> {
                Intent helpIntent = new Intent(getActivity(), TenantHelp.class);
                startActivity(helpIntent);
            });
        }

        // Set up Settings button
        View settingsButton = view.findViewById(R.id.settings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent settingsIntent = new Intent(getActivity(), TenantSettings.class);
                startActivity(settingsIntent);
            });
        }

        // Set up Edit Profile button
        View editProfileButton = view.findViewById(R.id.tEditProfile);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> {
                Intent editProfileIntent = new Intent(getActivity(), TenantEditProfile.class);
                startActivity(editProfileIntent);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
