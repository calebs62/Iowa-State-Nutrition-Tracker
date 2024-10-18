package com.example.a1_jubair_6_frontend.network;

import android.os.Handler;
import android.os.Looper;

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
    private WebSocketListener listener;
    private Handler handler;

    public WebSocketClient(WebSocketListener listener) {
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void connect(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
    }

    public void send(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        handler.post(() -> listener.onOpen(webSocket, response));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        handler.post(() -> listener.onMessage(webSocket, text));
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        handler.post(() -> listener.onClosing(webSocket, code, reason));
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        handler.post(() -> listener.onClosed(webSocket, code, reason));
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        handler.post(() -> listener.onFailure(webSocket, t, response));
    }
}
