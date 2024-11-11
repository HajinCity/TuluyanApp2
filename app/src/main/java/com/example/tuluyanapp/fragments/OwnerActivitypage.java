package com.example.tuluyanapp.fragments;

import android.content.Intent; // Import for Intent
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.Chatfunction;

public class OwnerActivitypage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public OwnerActivitypage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tenantActivitypage.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerActivitypage newInstance(String param1, String param2) {
        OwnerActivitypage fragment = new OwnerActivitypage();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_activitypage, container, false);

        ImageView messageBtn = view.findViewById(R.id.messageBtn2);
        messageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Chatfunction.class);
            startActivity(intent);
        });

        return view;
    }
}