package com.uts.onlineforum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, password, confirmPassword;
    private Button btnRegister, btnBack;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.et_username_register);
        password = findViewById(R.id.et_password_register);
        confirmPassword = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register_user);
        btnBack = findViewById(R.id.btn_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedPassword = hashPassword(pass);

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user);
            userData.put("password", hashedPassword);
            userData.put("gender", "-");
            userData.put("birthplace", "-");
            userData.put("status", "-");
            userData.put("hobby", "-");

            firestore.collection("users").document(user)
                    .set(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("RegisterActivity", "Error registering user", task.getException());
                        }
                    });
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
