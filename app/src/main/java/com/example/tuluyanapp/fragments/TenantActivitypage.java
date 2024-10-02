package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.Chatfunction;

public class TenantActivitypage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize anything you need here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_activitypage, container, false);

        ImageView messageBtn = view.findViewById(R.id.messageBtn2);
        messageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Chatfunction.class);
            startActivity(intent);
        });

        return view;
    }
}