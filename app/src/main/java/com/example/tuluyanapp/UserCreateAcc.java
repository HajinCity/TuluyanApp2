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

public class UserCreateAcc extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxPrivacy;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_acc);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                Toast.makeText(UserCreateAcc.this, "Please accept the privacy policy.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserCreateAcc.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            storeUserData(name, email);

                            Toast.makeText(UserCreateAcc.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(UserCreateAcc.this, UserLogin.class));
                            finish();
                        } else {
                            Toast.makeText(UserCreateAcc.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void storeUserData(String name, String email) {

        Map<String, Object> tenantData = new HashMap<>();
        tenantData.put("First-Name", name);
        tenantData.put("useraccount", email);
        tenantData.put("Address", "");
        tenantData.put("Age", "");
        tenantData.put("Birthdate", "");
        tenantData.put("Contact-No", "");
        tenantData.put("Last-Name", "");
        tenantData.put("Middle-Name", "");
        tenantData.put("email", email);
        tenantData.put("profilepic", "");
        tenantData.put("tenant", mAuth.getCurrentUser().getUid()); // Use UID directly

        // Store in Firestore under the collection "tenantcollection"
        db.collection("tenantcollection")
                .document(mAuth.getCurrentUser().getUid())  // Use the UID as the document ID
                .set(tenantData)
                .addOnSuccessListener(aVoid -> {
                    // Success message or any additional actions
                    Toast.makeText(UserCreateAcc.this, "User data stored successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure message
                    Toast.makeText(UserCreateAcc.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}