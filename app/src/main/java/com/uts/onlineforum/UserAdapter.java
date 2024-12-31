package com.uts.onlineforum;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<user> users;

    public UserAdapter(Context context) {
        this.context = context;
        this.users = new ArrayList<>();
    }

    public void setUsers(ArrayList<user> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user user = users.get(position);
        holder.bind(user);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_USER_NAME, user.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (users != null) ? users.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtDescription;
        private ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDescription = itemView.findViewById(R.id.txt_type);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }

        public void bind(user user) {
            txtName.setText(user.getName());
            txtDescription.setText(user.getType());
            Glide.with(context)
                    .load(user.getPhoto())
                    .into(imgPhoto);
        }
    }
}
