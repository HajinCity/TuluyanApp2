package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class OwnerLogin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;  // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        progressBar = new ProgressBar(this);

        // Set up login button click listener
        findViewById(R.id.button3).setOnClickListener(v -> {
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

            progressBar.setVisibility(View.VISIBLE); // Show progress bar when login starts

            // Authenticate owner
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OwnerLogin.this, task -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar after login completes
                        if (task.isSuccessful()) {
                            // Check if the owner exists in LandlordCollection
                            checkOwnerInFirestore();
                        } else {
                            Toast.makeText(OwnerLogin.this, "Authentication failed: " +
                                            Objects.requireNonNullElse(task.getException(), new Exception("Unknown error")).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Set up "Create Account" link click listener
        TextView textViewCreateAccount = findViewById(R.id.textView8);
        textViewCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerLogin.this, OwnerCreateAccActivity.class);
            startActivity(intent);
        });

        // Set up "Forgot Password" link click listener
        TextView textViewForgotPassword = findViewById(R.id.textView7);
        textViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerLogin.this, Ownerfgp.class);
            startActivity(intent);
        });
    }

    private void checkOwnerInFirestore() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("LandlordCollection").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Toast.makeText(OwnerLogin.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            // Redirect to another activity (e.g., main dashboard for owners)
                            startActivity(new Intent(OwnerLogin.this, MainActivity4.class));
                            finish();
                        } else {
                            Toast.makeText(OwnerLogin.this, "No owner data found.", Toast.LENGTH_LONG).show();
                            // Optionally, log out the user
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(OwnerLogin.this, "Failed to check owner data: " +
                                        Objects.requireNonNullElse(task.getException(), new Exception("Unknown error")).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
