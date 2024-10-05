package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.FoodAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {
    private RecyclerView foodList;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foodItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        foodList = view.findViewById(R.id.foodList);
        foodList.setLayoutManager(new LinearLayoutManager(requireContext()));

        foodAdapter = new FoodAdapter(foodItemList);
        foodList.setAdapter(foodAdapter);

        //This makes it so the food list cards don't clip over the nav bar
        int bottomNavHeight = getResources().getDimensionPixelSize(R.dimen.bottom_nav_height);
        foodList.setPadding(0, 0, 0, bottomNavHeight);

        Log.d("Quantity Info", getFoodItemQuantities().toString());

        //TODO: need to make it so the list updates its quantity when the increment or decrement is clicked

        //TODO: This is not how we should get the data, this is just to see if it works. Need to put each food items in their corresponding menu
        getAllFoodItems();
    }

    // <editor-fold desc="HTTP Requests">

    private void getAllFoodItems() {
        String url = AppConstants.SERVER_URL + "/item";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    foodItemList.clear();
                    for (int i = 0; i < response.length(); i++){
                        try{
                            FoodItem item = gson.fromJson(response.getJSONObject(i).toString(), FoodItem.class);
                            foodItemList.add(item);
                        }
                        catch (Exception e){
                            Log.e("Response Error", String.valueOf(e.getMessage()));
                        }
                    }
                    foodAdapter.notifyDataSetChanged();
                },
                error ->{
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void getFoodItemById(int id) {
        String url = AppConstants.SERVER_URL + "/item/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    FoodItem item = gson.fromJson(response.toString(), FoodItem.class);
                    //TODO: Do something with the item, this will probably be used for the search bar
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
    }

    private void createFoodItem(FoodItem foodItem) throws JSONException {
        String url = AppConstants.SERVER_URL + "/item";

        JSONObject jsonBody = new JSONObject(gson.toJson(foodItem));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    FoodItem createdItem = gson.fromJson(response.toString(), FoodItem.class);
                    foodItemList.add(createdItem);
                    foodAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateFoodItem(int id, FoodItem foodItem) throws JSONException {
        String url = AppConstants.SERVER_URL + "/item/update/" + id;

        JSONObject jsonBody = new JSONObject(gson.toJson(foodItem));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    FoodItem updatedItem = gson.fromJson(response.toString(), FoodItem.class);

                    // Update the item in the list
                    updateItemInList(updatedItem);
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateField(int id, String field, Object value) {
        String url = AppConstants.SERVER_URL + "/item/update/" + field + "/" + id;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("val", value);
        } catch (Exception e) {
            Log.e("JSON Error", String.valueOf(e.getMessage()));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    FoodItem updatedItem = gson.fromJson(response.toString(), FoodItem.class);
                    updateItemInList(updatedItem);
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void deleteFoodItem(int id) {
        String url = AppConstants.SERVER_URL + "/item/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    removeItemFromList(id);
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    // </editor-fold>

    // <editor-fold desc="Helper Methods">

    private void updateItemInList(FoodItem updatedItem) {
        for (int i = 0; i < foodItemList.size(); i++) {
            if (foodItemList.get(i).getId() == updatedItem.getId()) {
                foodItemList.set(i, updatedItem);
                foodAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void removeItemFromList(int id) {
        for (int i = 0; i < foodItemList.size(); i++) {
            if (foodItemList.get(i).getId() == id) {
                foodItemList.remove(i);
                foodAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    public Map<String, Integer> getFoodItemQuantities() {
        Map<String, Integer> quantities = new HashMap<>();

        for(FoodItem item : foodItemList){
            quantities.put(item.getName(), item.getQuantity());
        }
        return quantities;
    }
    //</editor-fold>
}