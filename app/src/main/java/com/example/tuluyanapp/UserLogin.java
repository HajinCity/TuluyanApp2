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

import java.util.Objects;

public class UserLogin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextTextPassword2);
        progressBar = new ProgressBar(this);

        // Set up login button click listener
        findViewById(R.id.button4).setOnClickListener(v -> {
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

            // Authenticate user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserLogin.this, task -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar after login completes
                        if (task.isSuccessful()) {
                            // Redirect to the tenant dashboard
                            handleSuccessfulLogin();
                        } else {
                            Toast.makeText(UserLogin.this, "Authentication failed: " +
                                            Objects.requireNonNullElse(task.getException(), new Exception("Unknown error")).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Set up "Create Account" link click listener
        TextView textViewCreateAccount = findViewById(R.id.textView11);
        textViewCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(UserLogin.this, UserCreateAccActivity.class);
            startActivity(intent);
        });

        // Set up "Forgot Password" link click listener
        TextView textViewForgotPassword = findViewById(R.id.textView10);
        textViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(UserLogin.this, Userfgp.class);
            startActivity(intent);
        });
    }

    private void handleSuccessfulLogin() {
        Toast.makeText(UserLogin.this, "Login successful.", Toast.LENGTH_SHORT).show();
        // Redirect to the tenant dashboard
        startActivity(new Intent(UserLogin.this, MainActivity3.class));
        finish();
    }
}
