package com.example.a1_jubair_6_frontend.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;
    Context context;
    private Gson gson = new Gson();
    private boolean isAdmin;

    public FoodAdapter(List<FoodItem> foodItemList, boolean isAdmin) {
        this.foodItemList = foodItemList;
        this.isAdmin = isAdmin;
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

        View adminActionsContainer = holder.itemView.findViewById(R.id.adminActionsContainer);
        adminActionsContainer.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

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

        if(isAdmin){
            holder.buttonEdit.setOnClickListener(v -> {
                    showEditDialog(position, foodItem);
            });

            holder.buttonDelete.setOnClickListener(v -> {
                showDeleteConfirmationDialog(position);
            });
        }

        holder.itemView.setOnClickListener(v -> showFoodDetailsDialog(foodItem));
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Food Item")
                .setMessage("Are you sure you want to delete this food item?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(position))
                .setNegativeButton("Cancel", null)
                .show();
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
        Button buttonEdit;
        Button buttonDelete;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.calories);
            quantity = itemView.findViewById(R.id.quantity);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonEdit = itemView.findViewById(R.id.btnEdit);
            buttonDelete = itemView.findViewById(R.id.btnDelete);
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

    private void showEditDialog(int position, FoodItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.food_item_dialog, null);

        TextInputEditText nameInput = dialogView.findViewById(R.id.editTextName);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.editTextDescription);
        TextInputEditText servingSizeInput = dialogView.findViewById(R.id.editTextServingSize);
        TextInputEditText caloriesInput = dialogView.findViewById(R.id.editTextCalories);
        TextInputEditText totalFatInput = dialogView.findViewById(R.id.editTextTotalFat);
        TextInputEditText sodiumInput = dialogView.findViewById(R.id.editTextSodium);
        TextInputEditText carbohydrateInput = dialogView.findViewById(R.id.editTextCarbohydrate);
        TextInputEditText proteinInput = dialogView.findViewById(R.id.editTextProtein);

        nameInput.setText(item.getName());
        descriptionInput.setText(item.getDescription());
        servingSizeInput.setText(item.getServingsize());
        caloriesInput.setText(String.valueOf(item.getCalories()));
        totalFatInput.setText(String.valueOf(item.getTotalFat()));
        sodiumInput.setText(String.valueOf(item.getSodium()));
        carbohydrateInput.setText(String.valueOf(item.getCarbohydrate()));
        proteinInput.setText(String.valueOf(item.getProtein()));

        AlertDialog dialog = builder
                .setView(dialogView)
                .setTitle("Edit Food Item")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                try {
                    FoodItem updatedItem = new FoodItem(
                            nameInput.getText().toString(),
                            Integer.parseInt(caloriesInput.getText().toString()),
                            Integer.parseInt(totalFatInput.getText().toString()),
                            Integer.parseInt(sodiumInput.getText().toString()),
                            Integer.parseInt(carbohydrateInput.getText().toString()),
                            Integer.parseInt(proteinInput.getText().toString()),
                            servingSizeInput.getText().toString(),
                            descriptionInput.getText().toString()
                    );

                    updatedItem.setId(item.getId());
                    updatedItem.setQuantity(item.getQuantity());

                    editItem(position, updatedItem);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Please fill all numeric fields correctly", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Error updating food item", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(
                        !nameInput.getText().toString().trim().isEmpty() &&
                                !servingSizeInput.getText().toString().trim().isEmpty() &&
                                !caloriesInput.getText().toString().trim().isEmpty() &&
                                !totalFatInput.getText().toString().trim().isEmpty() &&
                                !sodiumInput.getText().toString().trim().isEmpty() &&
                                !carbohydrateInput.getText().toString().trim().isEmpty() &&
                                !proteinInput.getText().toString().trim().isEmpty()
                );
            }
        };

        nameInput.addTextChangedListener(textWatcher);
        servingSizeInput.addTextChangedListener(textWatcher);
        caloriesInput.addTextChangedListener(textWatcher);
        totalFatInput.addTextChangedListener(textWatcher);
        sodiumInput.addTextChangedListener(textWatcher);
        carbohydrateInput.addTextChangedListener(textWatcher);
        proteinInput.addTextChangedListener(textWatcher);
    }

    private String getStringOrDefault(String value) {
        return value != null ? value : "N/A";
    }

    private int getIntOrDefault(Integer value) {
        return value != null ? value : 0;
    }

    public void deleteItem(int position){
        int id = foodItemList.get(position).getId();
        deleteFoodItem(id);
        foodItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, foodItemList.size());
    }

    public void editItem(int position, FoodItem updatedItem) throws JSONException {
        int id = updatedItem.getId();
        updateFoodItem(id, updatedItem);
        foodItemList.set(position, updatedItem);
        notifyItemChanged(position);
    }

    private void deleteFoodItem(int id) {
        String url = AppConstants.SERVER_URL + "/item/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Log.i("Food Item Deletion", "Food Item Deleted Successfully");
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void updateFoodItem(int id, FoodItem foodItem) throws JSONException {
        String url = AppConstants.SERVER_URL + "/item/update/" + id;

        JSONObject jsonBody = new JSONObject(gson.toJson(foodItem));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(context, "Successfully updated food item", Toast.LENGTH_SHORT).show();
                    Log.i("Food Item Update", "Food Item updated successfully");
                },
                error -> {
                    Toast.makeText(context, "Error updating food item [" + error.networkResponse.statusCode + "]", Toast.LENGTH_SHORT).show();
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
