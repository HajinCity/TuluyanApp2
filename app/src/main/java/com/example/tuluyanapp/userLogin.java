package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;  // Import TextView

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class userLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);  // Set your layout here

        // Handle insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button click to navigate to another activity (MainActivity3)
        findViewById(R.id.TTbutton3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        // TextView to navigate to userCreateAcc activity
        TextView textView = findViewById(R.id.TTtextView8); // Ensure the ID matches your XML
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, userCreateAcc.class);
                startActivity(intent);
            }
        });

        // TextView to navigate to userfgp activity
        TextView textView1 = findViewById(R.id.TTtextView7); // Ensure the ID matches your XML
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, userfgp.class); // Navigating to userfgp
                startActivity(intent);
            }
        });
    }
}
