package com.example.a1_jubair_6_frontend.managers;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodPlanManager {
    private static final String TAG = "FoodPlanManager";
    private final Context context;
    private final Gson gson;
    private FoodPlan currentPlan;
    private ProfileDataManager profileDataManager;
    private static final int REQUEST_TIMEOUT_MS = 10000;

    public interface FoodPlanCallback {
        void onSuccess(FoodPlan plan);
        void onError(String message);
    }

    public FoodPlanManager(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.profileDataManager = new ProfileDataManager(context);
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

    public void getFoodPlanFromGroup(FoodPlanCallback callback) {
        Log.d(TAG, "Starting getFoodPlanFromGroup request");
        String url = AppConstants.SERVER_URL + "/group/user/" + profileDataManager.getId();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject groupJson = new JSONObject(response);
                        Log.d(TAG, "Group response: " + groupJson.toString());

                        profileDataManager.setGroupId(groupJson.getInt("id"));

                        if (groupJson.isNull("plan")) {
                            Log.d(TAG, "No plan found in group data");
                            callback.onSuccess(null);
                            return;
                        }

                        JSONObject planJson = groupJson.getJSONObject("plan");
                        Log.d(TAG, "Found plan data: " + planJson.toString());

                        FoodPlan plan = createFoodPlanFromJson(planJson);
                        currentPlan = plan;
                        callback.onSuccess(plan);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing food plan: " + e.getMessage());
                        callback.onError("Error parsing food plan data");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.toString());
                    callback.onError("Failed to get food plan");
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private FoodPlan createFoodPlanFromJson(JSONObject planJson) throws JSONException {
        FoodPlan plan = new FoodPlan();
        plan.setId(planJson.getInt("id"));
        plan.setName(planJson.getString("name"));
        plan.setCalories(planJson.getInt("calories"));
        plan.setProtein(planJson.getInt("protein"));
        plan.setCarbohydrate(planJson.getInt("carbohydrate"));
        plan.setTotalFat(planJson.getInt("totalFat"));
        plan.setSodium(planJson.getInt("sodium"));
        Log.d(TAG, "Created food plan: " + plan.toString());
        return plan;
    }

    public FoodPlan getCurrentPlan() {
        return currentPlan;
    }

    public void clearCurrentPlan() {
        currentPlan = null;
    }
}
