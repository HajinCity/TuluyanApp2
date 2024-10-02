package com.example.tuluyanapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.fragments.OwnerActivitypage;
import com.example.tuluyanapp.fragments.OwnerHomepage;
import com.example.tuluyanapp.fragments.OwnerManagepostpage;
import com.example.tuluyanapp.fragments.OwnerPostpage;
import com.example.tuluyanapp.fragments.OwnerProfilepage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView btmnav1 = findViewById(R.id.bottom_navigation1);
        btmnav1.setSelectedItemId(R.id.Tnav_home);  // Corrected method name
        btmnav1.setOnItemSelectedListener(navListener1);  // Corrected method name

        Fragment selectedFragment1 = new OwnerHomepage();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, selectedFragment1).commit();
    }

    private final NavigationBarView.OnItemSelectedListener navListener1 = item -> {
        int itemId1 = item.getItemId();  // Corrected method to get item ID
        Fragment selected1 = null;

        if (itemId1 == R.id.Tnav_home) {
            selected1 = new OwnerHomepage();
        } else if (itemId1 == R.id.Tnav_post) {
            selected1 = new OwnerPostpage();
        } else if (itemId1 == R.id.Tnav_manage) {
            selected1 = new OwnerManagepostpage();
        } else if (itemId1 == R.id.Tnav_activity) {
            selected1 = new OwnerActivitypage();
        } else if (itemId1 == R.id.Tnav_profile) {
            selected1 = new OwnerProfilepage();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, selected1).commit();
        return true;
    };
}