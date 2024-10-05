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

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.FoodAdapter;
import com.example.a1_jubair_6_frontend.models.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {
    private RecyclerView foodList;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foodItemList = new ArrayList<>();

        //Mock data for testing
        foodItemList.add(new FoodItem("Food 1", 100, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 2", 150, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 3", 50, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 4", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 5", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 6", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 7", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 8", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 9", 200, 0, 0, 0, 0, "", ""));
        foodItemList.add(new FoodItem("Food 10", 200, 0, 0, 0, 0, "", ""));
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

    }

    public Map<String, Integer> getFoodItemQuantities() {
        Map<String, Integer> quantities = new HashMap<>();

        for(FoodItem item : foodItemList){
            quantities.put(item.getName(), item.getQuantity());
        }
        return quantities;
    }
}