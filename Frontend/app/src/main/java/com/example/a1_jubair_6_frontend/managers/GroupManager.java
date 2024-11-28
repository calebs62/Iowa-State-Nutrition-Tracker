package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupManager {
    private static final String TAG = "GroupManager";
    private final Context context;
    private final Gson gson;
    private final ProfileDataManager profileDataManager;

    public interface GroupCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public GroupManager(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.profileDataManager = new ProfileDataManager(context);
    }

    public void joinGroup(int groupId, GroupCallback callback) {
        String url = AppConstants.SERVER_URL + "/group/" + groupId + "/join";
        makeRequest(url, Request.Method.PUT, profileDataManager.getSessionToken(), null, callback);
    }

    public void leaveGroup(int groupId, GroupCallback callback) {
        String url = AppConstants.SERVER_URL + "/group/" + groupId + "/leave";
        String sessionToken = profileDataManager.getSessionToken();

        Log.d(TAG, "Attempting to leave group " + groupId + " with session token: " + sessionToken);

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    Log.d(TAG, "Leave group response: " + response);
                    try {
                        callback.onSuccess(new JSONObject(response));
                    } catch (JSONException e) {
                        callback.onError("Failed to parse response");
                    }
                },
                error -> {
                    String errorMessage = "Unknown error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    Log.e(TAG, "Leave group error: " + errorMessage);
                    callback.onError(errorMessage);
                }
        ) {
            @Override
            public byte[] getBody() {
                return sessionToken.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        request.setShouldRetryServerErrors(false);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getGroupDetails(int groupId, GroupCallback callback) {
        String url = AppConstants.SERVER_URL + "/group/" + groupId;
        makeRequest(url, Request.Method.GET, null, null, callback);
    }

    public void searchGroups(String keyword, GroupCallback callback) {
        String url = AppConstants.SERVER_URL + "/searchGroups?keyword=" + keyword;
        makeRequest(url, Request.Method.GET, null, null, callback);
    }

    private void makeRequest(String url, int method, String sessionToken, JSONObject requestBody, GroupCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                method,
                url,
                requestBody,
                response -> {
                    Log.d(TAG, "Request successful: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    Log.e(TAG, "Request failed: " + message);
                    callback.onError(message);
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                if (sessionToken != null) {
                    headers.put("Authorization", "Bearer " + sessionToken);
                }
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
