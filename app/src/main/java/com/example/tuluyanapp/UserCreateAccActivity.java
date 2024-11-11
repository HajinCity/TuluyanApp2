package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuluyanapp.fragments.TenantProfileClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserCreateAccActivity extends AppCompatActivity {

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

        // Initialize UI elements
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
                Toast.makeText(UserCreateAccActivity.this, "Please accept the privacy policy.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserCreateAccActivity.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

                            if (userId != null) {
                                Log.d("UserCreateAcc", "User UID: " + userId);

                                storeUserData(name, email);
                                Toast.makeText(UserCreateAccActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserCreateAccActivity.this, UserLogin.class));
                                finish();
                            } else {
                                Log.e("UserCreateAcc", "User authentication failed.");
                                Toast.makeText(UserCreateAccActivity.this, "User authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            Log.e("UserCreateAcc", "Registration failed: " + errorMessage);
                            Toast.makeText(UserCreateAccActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void storeUserData(String name, String email) {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("UserCreateAcc", "User is not authenticated.");
            Toast.makeText(UserCreateAccActivity.this, "User authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        TenantProfileClass user = new TenantProfileClass();
        user.setFirstName(name);
        user.setEmail(email);
        user.setTenantId(userId);

        Map<String, Object> tenantData = createTenantDataMap(user);

        db.collection("TenantCollection")
                .document(user.getTenantId())
                .set(tenantData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserCreateAcc", "User data stored successfully.");
                    Toast.makeText(UserCreateAccActivity.this, "User data stored successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UserCreateAcc", "Error storing user data", e);
                    Toast.makeText(UserCreateAccActivity.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private Map<String, Object> createTenantDataMap(TenantProfileClass user) {
        Map<String, Object> tenantData = new HashMap<>();
        tenantData.put("First-Name", user.getFirstName());
        tenantData.put("userAccount", user.getEmail());
        tenantData.put("tenant", user.getTenantId());
        return tenantData;
    }
}
