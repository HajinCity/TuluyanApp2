package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OwnerCreateAcc extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxPrivacy;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;  // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_create_acc);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link UI elements with the code
        editTextName = findViewById(R.id.editTextTextName);
        editTextEmail = findViewById(R.id.editTextTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextTextConfrim);
        Button buttonCreateAccount = findViewById(R.id.createAccountButton);
        checkBoxPrivacy = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar4);

        buttonCreateAccount.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            // Validate the inputs
            if (TextUtils.isEmpty(name)) {
                editTextName.setError("Name is required.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password is required.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                editTextConfirmPassword.setError("Passwords do not match.");
                return;
            }

            if (!checkBoxPrivacy.isChecked()) {
                Toast.makeText(OwnerCreateAcc.this, "Please accept the privacy policy.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Register user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OwnerCreateAcc.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Store user data in Firestore for landlords
                            storeOwnerData(name, email, password);

                            Toast.makeText(OwnerCreateAcc.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                            // Redirect to login page or main dashboard
                            startActivity(new Intent(OwnerCreateAcc.this, UserLogin.class));
                            finish();
                        } else {
                            Toast.makeText(OwnerCreateAcc.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void storeOwnerData(String name, String email, String password) {
        // Create a new landlord data map
        Map<String, Object> landlordData = new HashMap<>();
        String uid = mAuth.getCurrentUser().getUid(); // Get the UID of the newly created user

        landlordData.put("First-Name", name);
        landlordData.put("useraccount", email);
        landlordData.put("Address", "");
        landlordData.put("Contact-No", "");
        landlordData.put("Last-Name", "");
        landlordData.put("Middle-Name", "");
        landlordData.put("email", email);
        landlordData.put("password", password);  // Store the password
        landlordData.put("profilepic", "");
        landlordData.put("landlord", mAuth.getCurrentUser().getUid());

        // Store in Firestore under the collection "landlordcollection"
        db.collection("landlordcollection")
                .document(uid)  // Use the UID as the document ID
                .set(landlordData)
                .addOnSuccessListener(aVoid -> {
                    // Success message or any additional actions
                    Toast.makeText(OwnerCreateAcc.this, "Owner data stored successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure message
                    Toast.makeText(OwnerCreateAcc.this, "Error storing owner data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}