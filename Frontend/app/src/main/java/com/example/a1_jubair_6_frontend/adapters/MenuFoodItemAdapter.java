package com.example.a1_jubair_6_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class MenuFoodItemAdapter extends RecyclerView.Adapter<MenuFoodItemAdapter.ViewHolder> {
    private List<FoodItem> foodItems;
    private final OnFoodItemClickListener listener;

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodItem item);
    }

    public MenuFoodItemAdapter(List<FoodItem> foodItems, OnFoodItemClickListener listener) {
        this.foodItems = new ArrayList<>(foodItems);
        this.listener = listener;
    }

    public void updateFoodItems(List<FoodItem> newItems) {
        this.foodItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        holder.foodName.setText(item.getName());
        holder.calories.setText(String.format("%d Cal", item.getCalories()));
        holder.servingSize.setText(item.getServingsize());

        holder.itemView.setOnClickListener(v -> listener.onFoodItemClick(item));

        holder.deleteButton.setOnClickListener(v -> listener.onFoodItemClick(item));
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView calories;
        TextView servingSize;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.calories);
            servingSize = itemView.findViewById(R.id.servingSize);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
