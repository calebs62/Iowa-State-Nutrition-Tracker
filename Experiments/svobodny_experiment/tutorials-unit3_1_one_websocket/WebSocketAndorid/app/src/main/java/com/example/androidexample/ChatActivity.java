package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;
import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private String username;
    private String userColor;
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String HISTORY_KEY = "chat_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get username from intent
        username = getIntent().getStringExtra("username");
        // Generate a persistent color for this user
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userColor = prefs.getString("user_" + username + "_color", generateRandomColor());
        prefs.edit().putString("user_" + username + "_color", userColor).apply();

        /* initialize UI elements */
        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        msgTv = findViewById(R.id.tx1);

        // Restore chat history
        String savedHistory = prefs.getString(HISTORY_KEY, "");
        if (!savedHistory.isEmpty()) {
            msgTv.setText(savedHistory);
        }

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString();
                if (!message.trim().isEmpty()) {
                    // For now, just send the plain message since server expects plain text
                    WebSocketManager.getInstance().sendMessage(message);
                    msgEtx.setText(""); // Clear input after sending
                }
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
    }

    private String generateRandomColor() {
        // Generate a random color that's not too light (for readability)
        int red = (int)(Math.random() * 200);
        int green = (int)(Math.random() * 200);
        int blue = (int)(Math.random() * 200);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            try {
                // Try to parse as JSON first
                String formattedMessage;
                try {
                    JSONObject messageObj = new JSONObject(message);
                    formattedMessage = String.format(
                            "<font color='%s'>[%s] %s:</font> %s",
                            messageObj.getString("color"),
                            messageObj.getString("timestamp"),
                            messageObj.getString("username"),
                            messageObj.getString("message")
                    );
                } catch (Exception e) {
                    // If JSON parsing fails, treat as plain text
                    formattedMessage = String.format(
                            "<font color='%s'>[%s] %s:</font> %s",
                            userColor,
                            getCurrentTimestamp(),
                            username,
                            message
                    );
                }

                String currentText = msgTv.getText().toString();
                String newText = currentText + "\n" + formattedMessage;
                msgTv.setText(android.text.Html.fromHtml(newText));

                // Save chat history
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putString(HISTORY_KEY, newText).apply();

            } catch (Exception e) {
                Log.e("MessageParsing", "Error displaying message", e);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save chat history when app is paused
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(HISTORY_KEY, msgTv.getText().toString()).apply();
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            String disconnectMessage = String.format(
                    "\n<font color='#FF0000'>--- Connection closed by %s: %s ---</font>",
                    closedBy,
                    reason
            );
            msgTv.append(android.text.Html.fromHtml(disconnectMessage));
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> {
            String connectMessage = String.format(
                    "\n<font color='#008000'>--- Connected as %s ---</font>",
                    username
            );
            msgTv.append(android.text.Html.fromHtml(connectMessage));
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        runOnUiThread(() -> {
            String errorMessage = String.format(
                    "\n<font color='#FF0000'>--- Error: %s ---</font>",
                    ex.getMessage()
            );
            msgTv.append(android.text.Html.fromHtml(errorMessage));
        });
    }
}