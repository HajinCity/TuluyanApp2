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
    private FirebaseFirestore db;

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
                            // Check the user's existence in Firestore
                            String userUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            db.collection("LandlordCollection")
                                    .whereEqualTo("LandlordUID", userUID)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null && !task1.getResult().isEmpty()) {
                                            for (DocumentSnapshot doc : task1.getResult()) {
                                                if (doc.exists()) {
                                                    handleSuccessfulLogin();
                                                    return;
                                                }
                                            }
                                        }
                                        // Handle case where the user does not exist in the LandlordCollection
                                        Toast.makeText(OwnerLogin.this, "Account not found in LandlordCollection.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut(); // Sign out the user
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(OwnerLogin.this, "Failed to verify account: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        mAuth.signOut(); // Sign out the user
                                    });
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

    private void handleSuccessfulLogin() {
        Toast.makeText(OwnerLogin.this, "Login successful.", Toast.LENGTH_SHORT).show();
        // Redirect to the owner's dashboard
        startActivity(new Intent(OwnerLogin.this, MainActivity4.class));
        finish();
    }
}
