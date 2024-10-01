package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;  // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class userLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        TextView textView = findViewById(R.id.textView11);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, userCreateAcc.class);
                startActivity(intent);
            }
        });

        // Correcting the reference to textView1
        TextView textView1 = findViewById(R.id.textView10);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLogin.this, userfgp.class); // Navigating to userfgp
                startActivity(intent);
            }
        });
    }
}
