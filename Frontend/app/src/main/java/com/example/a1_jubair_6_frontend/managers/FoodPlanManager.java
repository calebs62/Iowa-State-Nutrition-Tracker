package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodPlanManager {
    private static final String TAG = "FoodPlanManager";
    private final Context context;
    private final Gson gson;
    private FoodPlan currentPlan;

    public interface FoodPlanCallback {
        void onSuccess(FoodPlan plan);
        void onError(String message);
    }

    public FoodPlanManager(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void createFoodPlan(FoodPlan plan, FoodPlanCallback callback) {
        String url = AppConstants.SERVER_URL + "/plan";

        try {
            JSONObject jsonBody = new JSONObject(gson.toJson(plan));

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        FoodPlan createdPlan = gson.fromJson(response.toString(), FoodPlan.class);
                        currentPlan = createdPlan;
                        callback.onSuccess(createdPlan);
                    },
                    error -> {
                        String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                        callback.onError("Failed to create plan: " + message);
                    }
            );

            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } catch (Exception e) {
            callback.onError("Error creating plan: " + e.getMessage());
        }
    }

    public void updateFoodPlan(int planId, JSONObject updates, FoodPlanCallback callback) {
        String url = AppConstants.SERVER_URL + "/plan/update/" + planId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                updates,
                response -> {
                    FoodPlan updatedPlan = gson.fromJson(response.toString(), FoodPlan.class);
                    currentPlan = updatedPlan;
                    callback.onSuccess(updatedPlan);
                },
                error -> {
                    String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    callback.onError("Failed to update plan: " + message);
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getFoodPlan(int planId, FoodPlanCallback callback) {
        String url = AppConstants.SERVER_URL + "/plan/" + planId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    FoodPlan plan = gson.fromJson(response.toString(), FoodPlan.class);
                    currentPlan = plan;
                    callback.onSuccess(plan);
                },
                error -> {
                    String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    callback.onError("Failed to get plan: " + message);
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getAllPlans(String keyword, FoodPlanCallback callback) {
        String url = AppConstants.SERVER_URL + "/allPlans?keyword=" + keyword;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<FoodPlan> plans = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            FoodPlan plan = gson.fromJson(response.getJSONObject(i).toString(), FoodPlan.class);
                            plans.add(plan);
                        }

                        callback.onSuccess(plans.isEmpty() ? null : plans.get(0));
                    } catch (Exception e) {
                        callback.onError("Error processing plans: " + e.getMessage());
                    }
                },
                error -> {
                    String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                    callback.onError("Failed to get plans: " + message);
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public FoodPlan getCurrentPlan() {
        return currentPlan;
    }
}
