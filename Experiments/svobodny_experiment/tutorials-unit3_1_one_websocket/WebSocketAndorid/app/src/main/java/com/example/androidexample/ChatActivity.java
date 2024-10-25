package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ScrollView;

import org.java_websocket.handshake.ServerHandshake;

import android.text.Html;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private String username;
    private String userColor;
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String HISTORY_KEY = "chat_history";
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userColor = prefs.getString("user_" + username + "_color", generateRandomColor());
        prefs.edit().putString("user_" + username + "_color", userColor).apply();

        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        msgTv = findViewById(R.id.tx1);
        scrollView = findViewById(R.id.scrollView);

        msgTv.setMovementMethod(new ScrollingMovementMethod());

        String savedHistory = prefs.getString(HISTORY_KEY, "");
        if (!savedHistory.isEmpty()) {
            msgTv.setText(Html.fromHtml(savedHistory));
        }

        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString().trim();
                if (!message.isEmpty()) {
                    WebSocketManager.getInstance().sendMessage(message);
                    msgEtx.setText("");
                }
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
    }

    private void appendMessage(String message) {
        StringBuilder currentText = new StringBuilder();
        if (msgTv.length() > 0) {
            currentText.append(msgTv.getText()).append("\n");
        }
        currentText.append(message);

        msgTv.setText(currentText.toString());

        // Scroll to bottom
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        // Save chat history
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(HISTORY_KEY, currentText.toString()).apply();
    }

    private String generateRandomColor() {
        int red = (int)(Math.random() * 200);
        int green = (int)(Math.random() * 200);
        int blue = (int)(Math.random() * 200);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            try {
                // Clean up the message if it contains multiple lines
                String[] lines = message.split("\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        appendMessage(line.trim());
                    }
                }
            } catch (Exception e) {
                Log.e("MessageParsing", "Error displaying message", e);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(HISTORY_KEY, msgTv.getText().toString()).apply();
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String disconnectMessage = "--- Connection closed by " + closedBy + ": " + reason + " ---";
            appendMessage(disconnectMessage);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> {
            String connectMessage = "--- Connected as " + username + " ---";
            appendMessage(connectMessage);
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        runOnUiThread(() -> {
            String errorMessage = "--- Error: " + ex.getMessage() + " ---";
            appendMessage(errorMessage);
        });
    }
}