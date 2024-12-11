package com.example.a1_jubair_6_frontend.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;

import org.json.JSONException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private WebSocketClient webSocketClient;
    private EditText msgEtx;
    private TextView chatMessages;
    private String username;
    private int userId;
    private ProfileDataManager profileDataManager;
    private int groupId;
    private TextView chatTitle;
    private ImageView backButton;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profileDataManager = new ProfileDataManager(this);
        groupId = getIntent().getIntExtra("groupId", -1);

        backButton = findViewById(R.id.backButton);
        sendBtn = findViewById(R.id.sendBtn);
        msgEtx = findViewById(R.id.msgEdt);
        chatMessages = findViewById(R.id.chatMessages);
        chatTitle = findViewById(R.id.chatTitle);

        getGroupName();
        username = profileDataManager.getFirstname() + profileDataManager.getLastname();
        userId = profileDataManager.getId();

        String url = AppConstants.WEBSOCKET_MESSAGE_SERVER_URL + userId + "/" + groupId;
        //String url = "ws://10.0.2.2:8080"; //Local hosted mock websocket

        webSocketClient = new WebSocketClient(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> chatMessages.append("\n" + text + "\n"));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket failure: " + t.getMessage());
            }
        });
        webSocketClient.connect(url);

        backButton.setOnClickListener(v -> {
            webSocketClient.close();
            finish();
        });

        sendBtn.setOnClickListener(v -> {
            String message = msgEtx.getText().toString().trim();
            if (!message.isEmpty()) {
                webSocketClient.send(message);
                msgEtx.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private void getGroupName() {
        String url = AppConstants.SERVER_URL + "/group/" + groupId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String groupName = response.getString("groupName");
                        chatTitle.setText(groupName);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.i("getGroupName Error", error.getMessage());
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
