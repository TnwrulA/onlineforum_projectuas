package com.uts.onlineforum;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_USER_NAME = "extra_user_name";

    private RecyclerView recyclerView;
    private EditText edtMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_chat);

        recyclerView = findViewById(R.id.recycler_view_chat);
        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString();
            if (!text.isEmpty()) {
                messageList.add(new Message(text, true));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                edtMessage.setText("");
            }
        });
    }
}
