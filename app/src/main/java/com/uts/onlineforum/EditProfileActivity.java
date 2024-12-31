package com.uts.onlineforum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etGender, etBirthplace, etStatus, etHobby;
    private ImageView ivProfileImage;
    private Button btnSave;
    private FirebaseFirestore firestore;
    private String loggedInUser;
    private String selectedDrawableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etGender = findViewById(R.id.et_edit_gender);
        etBirthplace = findViewById(R.id.et_edit_birthplace);
        etStatus = findViewById(R.id.et_edit_status);
        etHobby = findViewById(R.id.et_edit_hobby);
        ivProfileImage = findViewById(R.id.iv_profile_photo);
        btnSave = findViewById(R.id.btn_save_edit);

        firestore = FirebaseFirestore.getInstance();

        loggedInUser = getIntent().getStringExtra("username");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Toast.makeText(this, "Terjadi kesalahan, pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivProfileImage.setOnClickListener(v -> showImagePickerDialog());

        btnSave.setOnClickListener(v -> {
            String gender = etGender.getText().toString().trim();
            String birthplace = etBirthplace.getText().toString().trim();
            String status = etStatus.getText().toString().trim();
            String hobby = etHobby.getText().toString().trim();

            if (gender.isEmpty() || birthplace.isEmpty() || status.isEmpty() || hobby.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserProfile(gender, birthplace, status, hobby);
        });
    }

    private void showImagePickerDialog() {
        // Nama gambar yang akan muncul di dialog
        String[] imageOptions = {"Foto 1", "Foto 2", "Foto 3", "Foto 4", "Foto 5"};
        // Nama drawable yang akan disimpan di Firestore
        String[] drawableNames = {"profile_pic1", "profile_pic2", "profile_pic3", "profile_pic4", "profile_pic5"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Gambar Profil")
                .setItems(imageOptions, (dialog, which) -> {
                    selectedDrawableName = drawableNames[which];
                    int drawableId = getResources().getIdentifier(selectedDrawableName, "drawable", getPackageName());
                    ivProfileImage.setImageResource(drawableId);
                })
                .show();
    }

    private void updateUserProfile(String gender, String birthplace, String status, String hobby) {
        // Membuat hash map untuk data yang akan diperbarui
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("gender", gender);
        updates.put("birthplace", birthplace);
        updates.put("status", status);
        updates.put("hobby", hobby);

        if (selectedDrawableName != null) {
            updates.put("profileImageDrawable", selectedDrawableName);
        }

        // Update data di Firestore
        firestore.collection("users").document(loggedInUser).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal memperbarui data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
