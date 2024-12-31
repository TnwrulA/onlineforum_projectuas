package com.uts.onlineforum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private UserAdapter userAdapter;
    private ArrayList<user> users;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext());
        recyclerView.setAdapter(userAdapter);

        getUsersFromApi();

        return view;
    }

    private void getUsersFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/users";

        client.addHeader("Authorization", "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");
        client.addHeader("User-Agent", "request");

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (getActivity() == null) return;
                progressBar.setVisibility(View.INVISIBLE);
                String result = new String(responseBody);

                new Thread(() -> {
                    try {
                        JSONArray dataArray = new JSONArray(result);
                        ArrayList<user> listUsers = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataJson = dataArray.getJSONObject(i);
                            String name = dataJson.optString("login", "Unknown");
                            String type = dataJson.optString("type", "Unknown");
                            String photo = dataJson.optString("avatar_url", "");

                            user user = new user();
                            user.setPhoto(photo);
                            user.setName(name);
                            user.setType(type);
                            listUsers.add(user);
                        }

                        getActivity().runOnUiThread(() -> {
                            users.clear();
                            users.addAll(listUsers);
                            userAdapter.setUsers(users);

                            if (users.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error parsing data.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        });
                    }
                }).start();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (getActivity() == null) return;
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

                String errorMessage = (error != null && error.getMessage() != null) ? error.getMessage() : "Unknown Error";
                Toast.makeText(getContext(), statusCode + " : " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching data: " + errorMessage);
            }
        });
    }
}
