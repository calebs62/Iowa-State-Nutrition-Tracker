package com.example.a1_jubair_6_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class MenuFoodItemAdapter extends RecyclerView.Adapter<MenuFoodItemAdapter.ViewHolder> {
    private List<FoodItem> foodItems;
    private final OnFoodItemClickListener listener;
    private ProfileDataManager profileDataManager;
    private Context context;

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodItem item);
    }

    public MenuFoodItemAdapter(List<FoodItem> foodItems, OnFoodItemClickListener listener) {
        this.foodItems = foodItems != null ? new ArrayList<>(foodItems) : new ArrayList<>();
        this.listener = listener;
    }

    public void updateFoodItems(List<FoodItem> newItems) {
        this.foodItems = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_food, parent, false);
        profileDataManager = new ProfileDataManager(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);

        String name = item.getName() != null ? item.getName() : "Unknown";
        holder.foodName.setText(name);

        String calories = String.format("%d Cal", item.getCalories());
        holder.calories.setText(calories);

        String servingSize = item.getServingsize() != null ? item.getServingsize() : "N/A";
        holder.servingSize.setText(servingSize);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodItemClick(item);
            }
        });

        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < foodItems.size()) {
            foodItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodItems.size());
        }
    }

    public void addItem(FoodItem item) {
        if (item != null) {
            foodItems.add(item);
            notifyItemInserted(foodItems.size() - 1);
        }
    }

    public List<FoodItem> getFoodItems() {
        return new ArrayList<>(foodItems);
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