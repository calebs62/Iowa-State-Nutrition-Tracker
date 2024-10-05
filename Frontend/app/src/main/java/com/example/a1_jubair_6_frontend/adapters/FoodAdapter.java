package com.example.a1_jubair_6_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.FoodItem;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;

    public FoodAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.calories.setText(String.format("%d Cal", foodItem.getCalories()));
        holder.quantity.setText(String.valueOf(foodItem.getQuantity()));

        holder.buttonIncrease.setOnClickListener(v -> {
            foodItem.setQuantity(foodItem.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(foodItem.getQuantity()));
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            if(foodItem.getQuantity() > 0){
                foodItem.setQuantity(foodItem.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(foodItem.getQuantity()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView calories;
        TextView quantity;
        Button buttonDecrease;
        Button buttonIncrease;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.calories);
            quantity = itemView.findViewById(R.id.quantity);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
        }
    }
}
