package com.example.a1_jubair_6_frontend.adapters;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.FoodEaten;

import java.util.ArrayList;
import java.util.List;

public class FoodEatenAdapter extends RecyclerView.Adapter<FoodEatenAdapter.ViewHolder> {
    private static final String TAG = "FoodEatenAdapter";
    private List<FoodEaten> foodEatenList;
    private final OnFoodEatenDeleteListener deleteListener;

    public interface OnFoodEatenDeleteListener {
        void onDeleteClick(FoodEaten foodEaten);
    }

    public FoodEatenAdapter(OnFoodEatenDeleteListener deleteListener) {
        this.foodEatenList = new ArrayList<>();
        this.deleteListener = deleteListener;
    }

    public void updateFoodList(List<FoodEaten> newList) {
        this.foodEatenList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_eaten, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodEaten foodEaten = foodEatenList.get(position);
        holder.foodName.setText(foodEaten.getFood().getName());

        float calories = (float) (foodEaten.getFood().getCalories() * foodEaten.getServings());
        float servings = (float) foodEaten.getServings();

        holder.calories.setText(String.format("Calories: %.1f", calories));
        holder.servingSize.setText(String.format("Servings: %.1f", servings));

        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Delete clicked for food: " + foodEaten.getFood().getName());
            Log.d(TAG, "Food eaten ID: " + foodEaten.getId());

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Food")
                    .setMessage("Are you sure you want to delete this food entry?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Log.d(TAG, "Confirming delete for food ID: " + foodEaten.getId());
                        if (deleteListener != null) {
                            deleteListener.onDeleteClick(foodEaten);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return foodEatenList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, calories, servingSize;
        ImageButton deleteButton;

        ViewHolder(View view) {
            super(view);
            foodName = view.findViewById(R.id.foodName);
            calories = view.findViewById(R.id.calories);
            servingSize = view.findViewById(R.id.servingSize);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}
