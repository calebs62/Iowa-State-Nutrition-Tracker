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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.FoodEatenDataManager;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Adapter class for managing and displaying food items in a RecyclerView.
 * This class handles the display, editing, and deletion of food items, with different
 * functionality available for admin and non-admin users.
 *
 * @author Alexander Svobodny, Caleb Sanchez
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;
    Context context;
    private Gson gson = new Gson();
    private boolean isAdmin;
    private boolean isContributor;
    Menu currentMenu;
    ProfileDataManager profileDataManager;
    private FoodEatenDataManager foodEatenDataManager;
    private boolean isFromMenus;

    /**
     * Constructs a new FoodAdapter with the specified list of food items and admin status.
     *
     * @param foodItemList List of FoodItem objects to be displayed
     * @param isAdmin Boolean indicating whether the user has admin privileges
     */
    public FoodAdapter(List<FoodItem> foodItemList, boolean isAdmin, boolean isContributor, boolean isFromMenus) {
        this.foodItemList = foodItemList;
        this.isAdmin = isAdmin;
        this.isContributor = isContributor;
        this.isFromMenus = isFromMenus;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);

        profileDataManager = new ProfileDataManager(context);
        foodEatenDataManager = new FoodEatenDataManager(context);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);

        String name = foodItem.getName() != null ? foodItem.getName() : "Unknown";
        holder.foodName.setText(name);

        String caloriesText = String.format("%d Cal", foodItem.getCalories());
        holder.calories.setText(caloriesText);

        View adminActionsContainer = holder.itemView.findViewById(R.id.adminActionsContainer);

        if (!isFromMenus) {
            holder.buttonEat.setVisibility(View.GONE);
        }

        if (isAdmin && !isFromMenus) {
            adminActionsContainer.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.VISIBLE);
            holder.buttonDelete.setVisibility(View.VISIBLE);
        }
        else if(isContributor && !isFromMenus) {
            adminActionsContainer.setVisibility(View.VISIBLE);
            holder.buttonEdit.setVisibility(View.VISIBLE);
        }

        holder.buttonEat.setOnClickListener(v -> showServingsDialog(foodItem));

        if (isAdmin) {
            holder.buttonEdit.setOnClickListener(v -> showEditDialog(position, foodItem));
            holder.buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog(position));
        }
        else if (isContributor) {
            holder.buttonEdit.setOnClickListener(v -> showEditDialog(position, foodItem));
        }

        holder.itemView.setOnClickListener(v -> showFoodDetailsDialog(foodItem));
    }

    private void showServingsDialog(FoodItem foodItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_servings, null);

        NumberPicker servingsPicker = dialogView.findViewById(R.id.servingsPicker);
        servingsPicker.setMinValue(1);
        servingsPicker.setMaxValue(10);
        servingsPicker.setValue(1);

        builder.setView(dialogView)
                .setPositiveButton("Eat", (dialog, which) -> {
                    int servings = servingsPicker.getValue();
                    addFoodEaten(foodItem, servings);
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Shows a confirmation dialog before deleting a food item.
     *
     * @param position The position of the item to be deleted in the foodItemList
     */
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

    /**
     * ViewHolder class for food items in the RecyclerView.
     * Holds references to the views that display food item information.
     */
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView calories;
        Button buttonEat;
        Button buttonEdit;
        Button buttonDelete;

        /**
         * Constructs a new FoodViewHolder and initializes its views.
         *
         * @param itemView The View object containing the layout for a single food item
         */
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.calories);
            buttonEat = itemView.findViewById(R.id.btnEat);
            buttonEdit = itemView.findViewById(R.id.btnEdit);
            buttonDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    /**
     * Sets the current menu context for the adapter.
     *
     * @param menu The Menu object to associate with this adapter
     */
    public void setCurrentMenu(Menu menu){
        currentMenu = menu;
    }

    /**
     * Displays a dialog showing detailed nutritional information for a food item.
     *
     * @param item The FoodItem whose details should be displayed
     */
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

    /**
     * Displays a dialog for editing a food item's details.
     *
     * @param position The position of the item in the foodItemList
     * @param item The FoodItem to be edited
     */
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

    /**
     * Returns the provided string or a default value if the string is null.
     *
     * @param value The string to check
     * @return The original string if not null, "N/A" otherwise
     */
    private String getStringOrDefault(String value) {
        return value != null ? value : "N/A";
    }

    /**
     * Returns the provided integer or a default value if the integer is null.
     *
     * @param value The Integer to check
     * @return The original integer if not null, 0 otherwise
     */
    private int getIntOrDefault(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * Deletes a food item from the list and the server.
     *
     * @param position The position of the item to delete in the foodItemList
     */
    public void deleteItem(int position){
        int id = foodItemList.get(position).getId();
        if( currentMenu != null)
            deleteMenuFoodItem(id);
        else
            deleteFoodItem(id);
        foodItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, foodItemList.size());
    }

    /**
     * Updates a food item's details both in the list and on the server.
     *
     * @param position The position of the item to update in the foodItemList
     * @param updatedItem The new FoodItem data
     * @throws JSONException If there's an error creating the JSON request
     */
    public void editItem(int position, FoodItem updatedItem) throws JSONException {
        int id = updatedItem.getId();
        if(currentMenu != null)
            updateMenuFoodItem(id, updatedItem);
        else
            updateFoodItem(id, updatedItem);
        foodItemList.set(position, updatedItem);
        notifyItemChanged(position);
    }

    /**
     * Sends a DELETE request to remove a food item from the server.
     *
     * @param id The ID of the food item to delete
     */
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

    /**
     * Sends a PUT request to update a food item's details on the server.
     *
     * @param id The ID of the food item to update
     * @param foodItem The updated food item data
     * @throws JSONException If there's an error creating the JSON request
     */
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

    /**
     * Sends a DELETE request to remove a food item from a menu on the server.
     *
     * @param id The ID of the food item to remove from the menu
     */
    private void deleteMenuFoodItem(int id){
        String url = AppConstants.SERVER_URL + "/menu/" + currentMenu.getId() + "/remove/" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Log.i("Update Menu", "Successfully updated menu" + currentMenu);
                    Toast.makeText(context, "Successfully updated menu", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                    Toast.makeText(context, "Error updating menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Sends a PUT request to add/update a food item in a menu on the server.
     *
     * @param id The ID of the food item to add/update in the menu
     * @param foodItem The food item data to update
     */
    private void updateMenuFoodItem(int id, FoodItem foodItem){
        String url = AppConstants.SERVER_URL + "/menu/" + currentMenu.getId() + "/add/" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                response -> {
                    Log.i("Update Menu", "Successfully updated menu" + currentMenu);
                    Toast.makeText(context, "Successfully updated menu", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                    Toast.makeText(context, "Error updating menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void addFoodEaten(FoodItem foodItem, int servings) {
        foodEatenDataManager.addFoodEaten(foodItem, servings, new FoodEatenDataManager.FoodEatenCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context,
                        String.format("Added %d serving(s) of %s", servings, foodItem.getName()),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context,
                        "Failed to add food: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

