package com.example.a1_jubair_6_frontend.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebSocketClient extends WebSocketListener {
    private static final String TAG = "WebSocketClient";
    private WebSocket webSocket;
    private final WebSocketListener listener;
    private final Handler handler;
    private boolean isConnected = false;

    public WebSocketClient(WebSocketListener listener) {
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Connect to the WebSocket server.
     *
     * @param url The WebSocket server URL.
     */
    public void connect(String url) {
        if (isConnected) {
            Log.w(TAG, "WebSocket is already connected");
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
    }

    /**
     * Send a message through the WebSocket.
     *
     * @param message The message to send.
     */
    public void send(String message) {
        if (webSocket != null && isConnected) {
            webSocket.send(message);
        } else {
            Log.e(TAG, "WebSocket is not connected. Cannot send message.");
        }
    }

    /**
     * Close the WebSocket connection.
     */
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        isConnected = true;
        handler.post(() -> {
            Log.d(TAG, "WebSocket connected");
            listener.onOpen(webSocket, response);
        });
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        handler.post(() -> listener.onMessage(webSocket, text));
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        handler.post(() -> {
            isConnected = false;
            Log.d(TAG, "WebSocket closing: " + reason);
            listener.onClosing(webSocket, code, reason);
        });
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        isConnected = false;
        handler.post(() -> {
            Log.d(TAG, "WebSocket closed: " + reason);
            listener.onClosed(webSocket, code, reason);
        });
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        isConnected = false;
        handler.post(() -> {
            Log.e(TAG, "WebSocket failure: " + t.getMessage());
            listener.onFailure(webSocket, t, response);
        });
    }

    /**
     * Returns whether the WebSocket is connected.
     *
     * @return True if connected, false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }
}
