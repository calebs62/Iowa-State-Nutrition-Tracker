package com.example.a1_jubair_6_frontend.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.FoodItem;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;
    Context context;

    public FoodAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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

        holder.itemView.setOnClickListener(v -> showFoodDetailsDialog(foodItem));
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

    private void showFoodDetailsDialog(FoodItem item) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.nutrition_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView foodNameDialog = dialog.findViewById(R.id.foodNameDialog);
        TextView servingSize = dialog.findViewById(R.id.servingSize);
        TextView caloriesDialog = dialog.findViewById(R.id.caloriesDialog);
        TextView totalFat = dialog.findViewById(R.id.totalFat);
        TextView sodium = dialog.findViewById(R.id.sodium);
        TextView carbohydrate = dialog.findViewById(R.id.carbohydrate);
        TextView protein = dialog.findViewById(R.id.protein);
        ImageButton closeButton = dialog.findViewById(R.id.closeButton);

        foodNameDialog.setText(getStringOrDefault(item.getName()));
        servingSize.setText(String.format("Serving Size: %s", getStringOrDefault(item.getServingsize())));
        caloriesDialog.setText(String.format("Calories: %d", getIntOrDefault(item.getCalories())));
        totalFat.setText(String.format("Total Fat: %d", getIntOrDefault(item.getTotalFat())));
        sodium.setText(String.format("Sodium: %d", getIntOrDefault(item.getSodium())));
        carbohydrate.setText(String.format("Carbohydrate: %d", getIntOrDefault(item.getCarbohydrate())));
        protein.setText(String.format("Protein: %d", getIntOrDefault(item.getProtein())));

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private String getStringOrDefault(String value) {
        return value != null ? value : "N/A";
    }

    private int getIntOrDefault(Integer value) {
        return value != null ? value : 0;
    }
}
