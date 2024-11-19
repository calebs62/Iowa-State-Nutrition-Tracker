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
//import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {
    private EditText msgEtx;
    private TextView chatMessages;
    private WebSocketClient webSocketClient;
    private final String username = "username";  // Hardcoded username
    private final String groupChatId = "1";      // Hardcoded groupId
    private boolean isTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        //This will be the real string once its finished String url = AppConstants.SERVER_URL + "/chat/" + username + "/" + profileDataManager.getId() + "/" + groupChatId;
        String url = "wss://ws.postman-echo.com/raw";  // Replace with your backend URL when ready
        webSocketClient.connect(url);

        msgEtx.setOnKeyListener((v, keyCode, event) -> {
            if (!isTyping) {
                sendTypingStatus(true);
                isTyping = true;
            }
            return false;
        });

        sendBtn.setOnClickListener(v -> {
            if (isTyping) {
                sendTypingStatus(false);
                isTyping = false;
            }
            sendMessage();
        });
    }

    private void handleIncomingMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.getString("type");
            String messageUsername = jsonMessage.getString("username");

            switch (type) {
                case "chat":
                    String messageText = jsonMessage.getString("message");
                    String formattedMessage = messageUsername + ": " + messageText;
                    appendMessage(formattedMessage);
                    break;

                case "typing-started":
                    if (!messageUsername.equals(username)) {
                        appendMessage(messageUsername + " is typing...");
                    }
                    break;

                case "typing-stopped":
                    if (!messageUsername.equals(username)) {
                        removeTypingMessage(messageUsername);
                    }
                    break;
            }
        } catch (JSONException e) {
            Log.e("ChatActivity", "Error parsing message: " + e.getMessage());
            appendMessage(message);  // Fallback to displaying raw message
        }
    }

    private void sendMessage() {
        String messageText = msgEtx.getText().toString().trim();
        if (!messageText.isEmpty()) {
            try {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", "chat");
                jsonMessage.put("username", username);
                jsonMessage.put("groupId", groupChatId);
                jsonMessage.put("message", messageText);

                webSocketClient.send(jsonMessage.toString());
                msgEtx.setText("");
            } catch (JSONException e) {
                Log.e("ChatActivity", "Error creating message: " + e.getMessage());
            }
        }
    }

    private void sendTypingStatus(boolean typing) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", typing ? "typing-started" : "typing-stopped");
            jsonMessage.put("username", username);
            jsonMessage.put("groupId", groupChatId);

            webSocketClient.send(jsonMessage.toString());
        } catch (JSONException e) {
            Log.e("ChatActivity", "Error sending typing status: " + e.getMessage());
        }
    }

    private void appendMessage(String message) {
        String currentText = chatMessages.getText().toString();
        chatMessages.setText(currentText.isEmpty() ? message : currentText + "\n" + message);
    }

    private void removeTypingMessage(String username) {
        String currentText = chatMessages.getText().toString();
        String typingText = username + " is typing...";
        if (currentText.endsWith(typingText)) {
            chatMessages.setText(currentText.substring(0, currentText.length() - typingText.length()).trim());
        }
    }

    private void handleConnectionClosed(String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        appendMessage("---\nConnection closed by " + closedBy + "\nReason: " + reason);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketClient.close();
    }

    private String findGroupChat(String id) {
        switch (id) {
            case "1":
                return "Weight Loss Group";
            case "2":
                return "Weight Gain Group";
            case "3":
                return "Muscle Gain Group";
            default:
                return "Chat Group";
        }
    }
}




