package com.example.tuluyanapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.app.AppCompatDelegate;

public class Settings extends AppCompatActivity {
    private TextView rentTextView;
    private AppCompatButton privacyButton;
    private AppCompatButton changePassButton;
    private Switch darkModeSwitch;
    private Switch notifSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);  // Inflate the XML layout

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Initialize the UI components
        rentTextView = findViewById(R.id.Rent);
        privacyButton = findViewById(R.id.privacy);
        changePassButton = findViewById(R.id.changepass);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        notifSwitch = findViewById(R.id.switch_notif);

        // Set initial state based on shared preferences
        darkModeSwitch.setChecked(prefs.getBoolean("dark_mode", false));
        notifSwitch.setChecked(prefs.getBoolean("notifications", true));

        // Apply dark mode preference
        applyDarkMode(darkModeSwitch.isChecked());

        // Set click listeners for buttons
        privacyButton.setOnClickListener(v -> {
            // Navigate to privacy settings
        });
        changePassButton.setOnClickListener(v -> {
            // Navigate to change password settings
        });

        // Listeners for switches
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            applyDarkMode(isChecked);
        });

        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notifications", isChecked);
            editor.apply();
        });
    }

    private void applyDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
