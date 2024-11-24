package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodEaten;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodEatenDataManager {
    private static final String TAG = "FoodEatenDataManager";
    private final Context context;
    private final ProfileDataManager profileDataManager;
    private final Gson gson;
    private List<FoodEaten> foodEatenList;

    public interface FoodEatenCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface FoodEatenListCallback {
        void onSuccess(List<FoodEaten> foodEatenList);
        void onError(String message);
    }

    public FoodEatenDataManager(Context context) {
        this.context = context;
        this.profileDataManager = new ProfileDataManager(context);
        this.gson = new Gson();
        this.foodEatenList = new ArrayList<>();
    }

    public void addFoodEaten(FoodItem foodItem, FoodEatenCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        String url = AppConstants.SERVER_URL + "/foodEaten/add";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userId", profileDataManager.getId());
            jsonBody.put("foodId", foodItem.getId());
            jsonBody.put("servings", 1);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        FoodEaten newFoodEaten = gson.fromJson(response.toString(), FoodEaten.class);
                        foodEatenList.add(newFoodEaten);
                        callback.onSuccess();
                    },
                    error -> {
                        String message = error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
                        callback.onError("Failed to add food: " + message);
                    }
            );

            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } catch (Exception e) {
            callback.onError("Error preparing request: " + e.getMessage());
        }
    }

    public void removeFoodEaten(FoodItem foodItem, FoodEatenCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        String url = AppConstants.SERVER_URL + "/foodEaten/user/" + profileDataManager.getId() + "/recent/" + foodItem.getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    for (int i = foodEatenList.size() - 1; i >= 0; i--) {
                        if (foodEatenList.get(i).getFood().getId() == foodItem.getId()) {
                            foodEatenList.remove(i);
                            break;
                        }
                    }
                    callback.onSuccess();
                },
                error -> callback.onError("Failed to remove food: " + error.getMessage())
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getFoodEatenForTimeRange(long startTime, long endTime, FoodEatenListCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        String url = AppConstants.SERVER_URL + "/foodEaten/user/" + profileDataManager.getId() +
                "/range?start=" + startTime + "&end=" + endTime;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<FoodEaten> foods = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            FoodEaten food = gson.fromJson(response.getJSONObject(i).toString(), FoodEaten.class);
                            foods.add(food);
                        }
                        foodEatenList = foods;
                        callback.onSuccess(foods);
                    } catch (Exception e) {
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> callback.onError("Error fetching food data: " + error.getMessage())
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public List<FoodEaten> getFoodEatenList() {
        return new ArrayList<>(foodEatenList);
    }

    public void clearFoodEatenData() {
        foodEatenList.clear();
    }
}
