package com.example.a1_jubair_6_frontend.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {

    /*private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private String username;

    private boolean isTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");

        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        msgTv = findViewById(R.id.tx1);

        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        msgEtx.setOnKeyListener((v, keyCode, event) -> {
            if (!isTyping) {
                WebSocketManager.getInstance().sendMessage("{\"type\":\"typing-started\", \"user\":\"" + username + "\"}");
                isTyping = true;
            }

            return false;
        });

        sendBtn.setOnClickListener(v -> {
            if (isTyping) {
                WebSocketManager.getInstance().sendMessage("{\"type\":\"typing-stopped\", \"user\":\"" + username + "\"}");
                isTyping = false;
            }

            String message = msgEtx.getText().toString();
            if (!message.isEmpty()) {
                WebSocketManager.getInstance().sendMessage(message);
                msgEtx.setText("");
            }
        });
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            if (message.contains("\"type\":\"typing-started\"")) {
                onTypingStarted();
            } else if (message.contains("\"type\":\"typing-stopped\"")) {
                onTypingStopped();
            } else {
                String s = msgTv.getText().toString();
                msgTv.setText(s + "\n" + message);
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}

    public void onTypingStarted() {
        runOnUiThread(() -> {
            String currentText = msgTv.getText().toString();
            msgTv.setText(currentText + "\nA user is typing...");
        });
    }

    public void onTypingStopped() {
        runOnUiThread(() -> {
            String currentText = msgTv.getText().toString();
            String[] lines = currentText.split("\n");
            if (lines.length > 0) {
                StringBuilder updatedText = new StringBuilder();
                for (int i = 0; i < lines.length - 1; i++) {
                    updatedText.append(lines[i]).append("\n");
                }
                msgTv.setText(updatedText.toString());
            }
        });
    }
    */
}
