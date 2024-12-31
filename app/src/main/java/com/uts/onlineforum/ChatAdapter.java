package com.uts.onlineforum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isSender() ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).textMessage.setText(message.getMessage());
        } else {
            ((ReceiverViewHolder) holder).textMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }
}
