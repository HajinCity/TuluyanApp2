package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OwnerLogin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link UI elements with code
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        progressBar = new ProgressBar(this);

        Button loginButton = findViewById(R.id.button3);

        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password is required.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Sign in user using Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OwnerLogin.this, task -> {
                        if (task.isSuccessful()) {
                            // Fetch user data from Firestore for the authenticated landlord
                            fetchOwnerData();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OwnerLogin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Set up "Create Account" link
        TextView textView = findViewById(R.id.textView8);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerLogin.this, OwnerCreateAcc.class);
            startActivity(intent);
        });

        // Set up "Forgot Password" link
        TextView textView1 = findViewById(R.id.textView7);
        textView1.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerLogin.this, Ownerfgp.class);
            startActivity(intent);
        });
    }

    private void fetchOwnerData() {
        // Get the UID of the current user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch landlord data from Firestore
        db.collection("landlordcollection")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the data and proceed to the dashboard or next activity
                            String name = document.getString("First-Name");
                            Toast.makeText(OwnerLogin.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                            // Redirect to MainActivity4 (dashboard or main screen for landlords)
                            Intent intent = new Intent(OwnerLogin.this, MainActivity4.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OwnerLogin.this, "No such landlord exists in the database.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OwnerLogin.this, "Failed to retrieve landlord data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start MainActivity3
                Intent intent = new Intent(OwnerLogin.this, MainActivity4.class);
                startActivity(intent);
            }
        });

        TextView textView = findViewById(R.id.textView8);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OwnerLogin.this, OwnerCreateAcc.class);
                startActivity(intent);
            }
        });
        TextView textView1 = findViewById(R.id.textView7);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OwnerLogin.this, Ownerfgp.class);
                startActivity(intent);
            }
        });
    }
}