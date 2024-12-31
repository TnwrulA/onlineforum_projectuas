package com.uts.onlineforum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.uts.onlineforum.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.settingProfile.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", getContext().MODE_PRIVATE);
            String username = sharedPreferences.getString("loggedInUser", null);

            if (username == null || username.isEmpty()) {
                Toast.makeText(getContext(), "Nama pengguna tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        binding.settingDarkMode.setOnClickListener(v -> toggleDarkMode());

        binding.settingLogout.setOnClickListener(v -> logoutUser());

        return root;
    }

    private void toggleDarkMode() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int currentMode = AppCompatDelegate.getDefaultNightMode();

        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(getContext(), "Mode Terang Diaktifkan", Toast.LENGTH_SHORT).show();
            editor.putBoolean("dark_mode", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(getContext(), "Mode Gelap Diaktifkan", Toast.LENGTH_SHORT).show();
            editor.putBoolean("dark_mode", true);
        }
        editor.apply();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Anda telah keluar", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
