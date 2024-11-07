package com.example.a1_jubair_6_frontend.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {

    private EditText msgEtx;
    private TextView chatMessages;
    private WebSocketClient webSocketClient;
    private String username;
    private boolean isTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        username = "username";
        String groupChatId = "Group Chat";


        ImageView backButton = findViewById(R.id.backButton);
        Button sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        chatMessages = findViewById(R.id.chatMessages);
        TextView chatTitle = findViewById(R.id.chatTitle);

        chatTitle.setText(findGroupChat(groupChatId));

        backButton.setOnClickListener(v -> {
            webSocketClient.close();
            Intent exploreIntent = new Intent(this, BaseActivity.class);
            exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        });

        webSocketClient = new WebSocketClient(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                runOnUiThread(() -> Log.d("ChatActivity", "Connected to WebSocket server"));
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> handleIncomingMessage(text));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                runOnUiThread(() -> handleConnectionClosed(reason, true));
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                runOnUiThread(() -> handleConnectionClosed(reason, false));
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                runOnUiThread(() -> Log.e("ChatActivity", "WebSocket Error: " + t.getMessage()));
            }
        });

        String url = "wss://ws.postman-echo.com/raw";
        webSocketClient.connect(url);

        msgEtx.setOnKeyListener((v, keyCode, event) -> {
            if (!isTyping) {
                webSocketClient.send("{\"type\":\"typing-started\", \"user\":\"" + username + "\"}");
                isTyping = true;
            }
            return false;
        });

        sendBtn.setOnClickListener(v -> {
            if (isTyping) {
                webSocketClient.send("{\"type\":\"typing-stopped\", \"user\":\"" + username + "\"}");
                isTyping = false;
            }

            String message = msgEtx.getText().toString();
            if (!message.isEmpty()) {
                webSocketClient.send(message);
                msgEtx.setText("");
            }
        });
    }

    private void handleIncomingMessage(String message) {
        if (message.contains("\"type\":\"typing-started\"")) {
            onTypingStarted();
        } else if (message.contains("\"type\":\"typing-stopped\"")) {
            onTypingStopped();
        } else {
            String s = chatMessages.getText().toString();
            chatMessages.setText(s + "\n" + message);
        }
    }

    private void handleConnectionClosed(String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        String s = chatMessages.getText().toString();
        chatMessages.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
    }

    private void onTypingStarted() {
        String currentText = chatMessages.getText().toString();
        chatMessages.setText(currentText + "\nA user is typing...");
    }

    private void onTypingStopped() {
        String currentText = chatMessages.getText().toString();
        String[] lines = currentText.split("\n");
        if (lines.length > 0) {
            StringBuilder updatedText = new StringBuilder();
            for (int i = 0; i < lines.length - 1; i++) {
                updatedText.append(lines[i]).append("\n");
            }
            chatMessages.setText(updatedText.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketClient.close();
    }

    private String findGroupChat(String id){
        if(id.equals("1")){
            return "Weight Loss Group";
        }
        else if(id.equals("2")){
            return "Weight Gain Group";
        }
        else if(id.equals("3")){
            return "Muscle Gain Group";
        }
        else{
            Log.e("Error Finding Group", "Could not find the group, returning blank string.");
            return "";
        }
    }
}




