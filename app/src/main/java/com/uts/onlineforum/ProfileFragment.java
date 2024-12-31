package com.uts.onlineforum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private TextView tvUsername, tvGender, tvBirthplace, tvStatus, tvHobby;
    private ImageView ivProfileImage;
    private Button btnEditProfile;
    private FirebaseFirestore firestore;
    private String loggedInUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firestore = FirebaseFirestore.getInstance();

        tvUsername = view.findViewById(R.id.tv_username);
        tvGender = view.findViewById(R.id.tv_gender);
        tvBirthplace = view.findViewById(R.id.tv_birthplace);
        tvStatus = view.findViewById(R.id.tv_status);
        tvHobby = view.findViewById(R.id.tv_hobby);
        ivProfileImage = view.findViewById(R.id.iv_profile_photo);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        loggedInUser = prefs.getString("loggedInUser", null);

        if (loggedInUser == null) {
            Toast.makeText(getActivity(), "Tidak ada pengguna yang login", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("username", loggedInUser);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        firestore.collection("users").document(loggedInUser)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvUsername.setText(loggedInUser);
                        tvGender.setText(documentSnapshot.getString("gender"));
                        tvBirthplace.setText(documentSnapshot.getString("birthplace"));
                        tvStatus.setText(documentSnapshot.getString("status"));
                        tvHobby.setText(documentSnapshot.getString("hobby"));

                        String drawableName = documentSnapshot.getString("profileImageDrawable");
                        if (drawableName != null && !drawableName.isEmpty()) {
                            int drawableId = getResources().getIdentifier(drawableName, "drawable", requireActivity().getPackageName());
                            ivProfileImage.setImageResource(drawableId);
                        } else {
                            ivProfileImage.setImageResource(R.drawable.default_profile);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Gagal memuat data pengguna: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUserProfile();
    }
}
