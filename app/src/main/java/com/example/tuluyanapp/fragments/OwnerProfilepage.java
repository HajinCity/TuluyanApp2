package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.MainActivity2;
import com.example.tuluyanapp.R;
import com.example.tuluyanapp.fragments.OwnerEditProfile;
import com.example.tuluyanapp.fragments.OwnerSettings;

public class OwnerProfilepage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public OwnerProfilepage() {
        // Required empty public constructor
    }

    public static OwnerProfilepage newInstance(String param1, String param2) {
        OwnerProfilepage fragment = new OwnerProfilepage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_owner_profilepage, container, false);

        // Find the settings icon ImageView
        ImageView settingsIcon = view.findViewById(R.id.Owner_settings_icon);

        // Set an OnClickListener for the ImageView to show the popup menu
        settingsIcon.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu instance with the provided view
        PopupMenu popup = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popup.getMenu());

        // Set a click listener for popup menu items
        popup.setOnMenuItemClickListener(item -> onPopupMenuItemClick(item));

        // Show the popup menu
        popup.show();
    }

    private boolean onPopupMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_profile) {
            // Start OwnerEditProfile activity
            Intent editProfileIntent = new Intent(getActivity(), OwnerEditProfile.class);
            startActivity(editProfileIntent);
            return true;
        } else if (id == R.id.settings) {
            // Start OwnerSettings activity
            Intent settingsIntent = new Intent(getActivity(), OwnerSettings.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.logout) {
            // Perform logout logic and navigate to MainActivity2
            Intent logoutIntent = new Intent(getActivity(), MainActivity2.class);
            startActivity(logoutIntent);
            requireActivity().finish();
            return true;
        }
        return false;
    }
}
