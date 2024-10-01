package com.example.tuluyanapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.tuluyanapp.fragments.tenantactivitypage;
import com.example.tuluyanapp.fragments.tenanthomepage;
import com.example.tuluyanapp.fragments.tenantmappage;
import com.example.tuluyanapp.fragments.tenantprofilepage;
import com.example.tuluyanapp.fragments.tenantsearchpage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView btmnav = findViewById(R.id.bottom_navigation);
        btmnav.setSelectedItemId(R.id.nav_home);  // Corrected method name
        btmnav.setOnItemSelectedListener(navListener);  // Corrected method name

        Fragment selectedFragment = new tenanthomepage();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();  // Corrected method to get item ID
        Fragment selected = null;

        if (itemId == R.id.nav_home) {
            selected = new tenanthomepage();
        } else if (itemId == R.id.nav_map) {
            selected = new tenantmappage();
        } else if (itemId == R.id.nav_search) {
            selected = new tenantsearchpage();
        } else if (itemId == R.id.nav_activity) {
            selected = new tenantactivitypage();
        } else if (itemId == R.id.nav_profile) {
            selected = new tenantprofilepage();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
        return true;
    };
}