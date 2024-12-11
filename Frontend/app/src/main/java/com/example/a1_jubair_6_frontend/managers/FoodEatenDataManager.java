package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodEaten;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, jsonContext) -> {
                    try {
                        if (json.isJsonNull()) {
                            return null;
                        }
                        String dateStr = json.getAsString();
                        Date parsedDate = FoodEaten.parseDate(dateStr);
                        if (parsedDate == null) {
                            throw new JsonParseException("Unable to parse date: " + dateStr);
                        }
                        return parsedDate;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new JsonParseException("Error parsing date", e);
                    }
                })
                .create();

        this.foodEatenList = new ArrayList<>();
    }

    public void addFoodEaten(FoodItem foodItem, int servings, FoodEatenCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        String url = AppConstants.SERVER_URL + "/eaten";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userId", profileDataManager.getId());
            jsonBody.put("foodId", foodItem.getId());
            jsonBody.put("servings", servings);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        FoodEaten newFoodEaten = gson.fromJson(response.toString(), FoodEaten.class);
                        foodEatenList.add(newFoodEaten);
                        createFoodActivity(newFoodEaten);
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

    public void removeFoodEaten(int foodEatenId, FoodEatenCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        String url = AppConstants.SERVER_URL + "/eaten/" + foodEatenId;

        Log.d(TAG, "Attempting to delete food eaten with ID: " + foodEatenId);
        Log.d(TAG, "Delete URL: " + url);

        JsonRequest<Boolean> request = new JsonRequest<Boolean>(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Log.d(TAG, "Successfully deleted food eaten with ID: " + foodEatenId);
                    foodEatenList.removeIf(item -> item.getId() == foodEatenId);
                    callback.onSuccess();
                },
                error -> {
                    Log.e(TAG, "Error deleting food: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error data: " + new String(error.networkResponse.data));
                    }
                    callback.onError("Failed to remove food: " + error.getMessage());
                }
        ) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(Boolean.parseBoolean(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getFoodEatenForTimeRange(Date startTime, Date endTime, FoodEatenListCallback callback) {
        if (profileDataManager.getId() == -1) {
            callback.onError("User not logged in");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTimeStr = dateFormat.format(startTime);
        String endTimeStr = dateFormat.format(endTime);

        String url = AppConstants.SERVER_URL + "/eaten/user/" + profileDataManager.getId() +
                "/time?startTime=" + startTimeStr + "&endTime=" + endTimeStr;

        Log.d(TAG, "Getting Food Eaten Data from url: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<FoodEaten> foods = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            FoodEaten food = gson.fromJson(response.getJSONObject(i).toString(), FoodEaten.class);
                            if (food.getTime() == null) {
                                Log.e("FoodEatenDataManager", "Parsed food has null date: " + response.getJSONObject(i).toString());
                                continue;
                            }
                            foods.add(food);
                        }
                        foodEatenList = foods;
                        callback.onSuccess(foods);
                    } catch (Exception e) {
                        Log.e("FoodEatenDataManager", "Error parsing response: " + e.getMessage());
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> callback.onError("Error fetching food data: " + error.getMessage())
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void createFoodActivity(FoodEaten foodEaten) {
        String url = AppConstants.SERVER_URL + "/activity/food/"
                + profileDataManager.getId() + "/"
                + profileDataManager.getGroupId();

        try {
            JSONObject foodActivityRequest = new JSONObject();
            foodActivityRequest.put("food", foodEaten.getFood().getName());
            foodActivityRequest.put("mealType", "meal");

            String nutritionInfo = String.format("Calories: %.1f • Servings: %.1f",
                    foodEaten.getFood().getCalories() * foodEaten.getServings(),
                    foodEaten.getServings());
            foodActivityRequest.put("nutritionInfo", nutritionInfo);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    foodActivityRequest,
                    response -> {
                        Log.d(TAG, "Food activity created successfully: " + response.toString());
                    },
                    error -> {
                        String errorMessage = "Error creating food activity: ";
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode != 200) {
                                errorMessage += "Status Code: " + error.networkResponse.statusCode;
                                Log.e(TAG, errorMessage);
                            } else {
                                Log.d(TAG, "Food activity created successfully");
                            }
                        } else {
                            errorMessage += error.toString();
                            Log.e(TAG, errorMessage);
                        }
                    }
            );
            VolleySingleton.getInstance(context).addToRequestQueue(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating food activity request: " + e.toString());
        }
    }

    public List<FoodEaten> getFoodEatenList() {
        return new ArrayList<>(foodEatenList);
    }

    public void clearFoodEatenData() {
        foodEatenList.clear();
    }
}
