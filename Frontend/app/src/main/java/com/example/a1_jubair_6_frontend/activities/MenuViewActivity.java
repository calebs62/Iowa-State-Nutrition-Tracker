package com.example.a1_jubair_6_frontend.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.MenuFoodItemAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MenuViewActivity extends AppCompatActivity {
    private Spinner menuSpinner;
    private EditText locationEdit;
    private EditText mealTypeEdit;
    private EditText dateEdit;
    private RecyclerView foodItemsRecyclerView;
    private Button addFoodButton;
    private Button saveButton;
    private Button addMenuButton;
    private Button deleteMenuButton;

    private ProfileDataManager profileDataManager;
    private List<Menu> menus = new ArrayList<>();
    private List<FoodItem> availableFoodItems = new ArrayList<>();
    private MenuFoodItemAdapter foodAdapter;
    private Menu currentMenu;
    private Gson gson = new Gson();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private boolean isAdmin;
    private boolean isContributor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);
        profileDataManager = new ProfileDataManager(this);

        isAdmin = profileDataManager.getAccountType().equals("ADMINISTRATOR");
        isContributor = profileDataManager.getAccountType().equals("CONTRIBUTOR");

        initializeViews();
        setupRecyclerView();
        fetchMenus();
        fetchAvailableFoodItems();
        setupListeners();
    }

    private void initializeViews() {
        menuSpinner = findViewById(R.id.menuSpinner);
        locationEdit = findViewById(R.id.locationEdit);
        mealTypeEdit = findViewById(R.id.mealTypeEdit);
        dateEdit = findViewById(R.id.dateEdit);
        foodItemsRecyclerView = findViewById(R.id.foodItemsRecyclerView);
        addFoodButton = findViewById(R.id.addFoodButton);
        saveButton = findViewById(R.id.saveButton);
        addMenuButton = findViewById(R.id.btnAddMenu);
        deleteMenuButton = findViewById(R.id.deleteMenuButton);

        if(isAdmin) {
            deleteMenuButton.setVisibility(View.VISIBLE);
        }

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        if(isAdmin) {
            foodAdapter = new MenuFoodItemAdapter(new ArrayList<>(), this::removeFoodItemFromMenu);
        }
        else if(isContributor) {
            foodAdapter = new MenuFoodItemAdapter(new ArrayList<>(), this::removeFoodItemFromMenu);
        }
        foodItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemsRecyclerView.setAdapter(foodAdapter);
    }

    private void setupListeners() {
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Menu selectedMenu = menus.get(position);
                fetchMenuById(selectedMenu.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dateEdit.setOnClickListener(v -> showDatePicker());
        addFoodButton.setOnClickListener(v -> showAddDialog());
        saveButton.setOnClickListener(v -> saveMenuChanges());
        addMenuButton.setOnClickListener(v -> showMenuAddDialog());
        deleteMenuButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showMenuAddDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_menu_item, null);

        Button addButton = view.findViewById(R.id.createMenuButton);
        EditText name = view.findViewById(R.id.menuNameEdit);
        EditText location = view.findViewById(R.id.menuLocationEdit);
        EditText mealType = view.findViewById(R.id.menuMealEdit);
        EditText date = view.findViewById(R.id.menuDateEdit);
        TextView formError = view.findViewById(R.id.tvFormError);

        dialog.setContentView(view);
        dialog.show();

        addButton.setOnClickListener(v -> {
            String menuName = name.getText().toString();
            String menuLocation = location.getText().toString();
            String menuMealType = mealType.getText().toString().toLowerCase();
            String menuDateStr = date.getText().toString();

            formError.setVisibility(View.GONE);

            if (menuName.isEmpty() || menuLocation.isEmpty() || menuMealType.isEmpty() || menuDateStr.isEmpty()) {
                formError.setText("All fields are required!");
                formError.setVisibility(View.VISIBLE);
                return;
            }

            if (!menuMealType.equals("breakfast") && !menuMealType.equals("lunch") && !menuMealType.equals("dinner")) {
                formError.setText("Invalid meal type! Use breakfast, lunch, or dinner.");
                formError.setVisibility(View.VISIBLE);
                return;
            }

            try {
                LocalDate menuDate = LocalDate.parse(menuDateStr, dateFormatter);
                Menu newMenu = new Menu(menuName, menuLocation, menuMealType, menuDate.format(dateFormatter));
                postNewMenuToServer(newMenu);
                dialog.dismiss();
            } catch (DateTimeParseException e) {
                formError.setText("Invalid date format!");
                formError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDate selectedDate = LocalDate.ofEpochDay(selection / (24 * 60 * 60 * 1000));
            dateEdit.setText(selectedDate.format(dateFormatter));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showAddDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_food_item, null);
        RecyclerView availableFoodRecyclerView = view.findViewById(R.id.availableFoodRecyclerView);
        EditText searchEdit = view.findViewById(R.id.searchEdit);

        List<FoodItem> filteredList = new ArrayList<>(availableFoodItems);

        MenuFoodItemAdapter adapter = new MenuFoodItemAdapter(filteredList, item -> {
            addFoodItemToMenu(item);
            dialog.dismiss();
        });

        availableFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        availableFoodRecyclerView.setAdapter(adapter);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                List<FoodItem> filtered = filterFoodItems(searchText);
                adapter.updateFoodItems(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private List<FoodItem> filterFoodItems(String searchText) {
        if (searchText.isEmpty()) {
            return new ArrayList<>(availableFoodItems);
        }

        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : availableFoodItems) {
            // Search in name and description
            if ((item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText))) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private void fetchMenus() {
        String url = AppConstants.SERVER_URL + "/allMenus";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    menus.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Menu menu = gson.fromJson(response.getJSONObject(i).toString(), Menu.class);
                            menus.add(menu);
                        } catch (Exception e) {
                            Log.e("MenuView", "Error parsing menu: " + e.getMessage());
                        }
                    }
                    updateMenuSpinner();
                },
                error -> Toast.makeText(this, "Error fetching menus", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchAvailableFoodItems() {
        String url = AppConstants.SERVER_URL + "/item";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.PUT,
                url,
                null,
                response -> {
                    availableFoodItems.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            FoodItem item = gson.fromJson(response.getJSONObject(i).toString(), FoodItem.class);
                            availableFoodItems.add(item);
                        } catch (Exception e) {
                            Log.e("MenuView", "Error parsing food item: " + e.getMessage());
                        }
                    }
                },
                error -> Toast.makeText(this, "Error fetching food items", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateMenuSpinner() {
        ArrayAdapter<Menu> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, menus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
    }

    private void updateUIWithMenu(Menu menu) {
        try {
            locationEdit.setText(menu.getLocation());
            mealTypeEdit.setText(menu.getMeal());

            String dateStr = menu.getDate();
            if (dateStr != null && !dateStr.isEmpty()) {
                dateEdit.setText(dateStr);
            } else {
                dateEdit.setText("");
            }

            if (menu.getFoodItems() != null) {
                foodAdapter.updateFoodItems(new ArrayList<>(menu.getFoodItems()));
            } else {
                foodAdapter.updateFoodItems(new ArrayList<>());
            }

            currentMenu = menu;
        } catch (Exception e) {
            Log.e("MenuView", "Error updating UI with menu", e);
            Toast.makeText(this, "Error displaying menu details", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        String displayText = String.format("%s - %s",
                currentMenu.getLocation(),
                currentMenu.getDate() != null ? currentMenu.getDate() : "No Date");

        new AlertDialog.Builder(this)
                .setTitle("Delete Menu")
                .setMessage("Are you sure you want to delete the menu " + displayText +"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCurrentMenuFromServer())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveMenuChanges() {
        if (currentMenu == null) return;

        currentMenu.setLocation(locationEdit.getText().toString());
        currentMenu.setMeal(mealTypeEdit.getText().toString());
        currentMenu.setDate(dateEdit.getText().toString());

        updateMenuOnServer();
    }

    private void updateMenuOnServer() {
        if (currentMenu == null) return;

        String url = AppConstants.SERVER_URL + "/menu/update/" + currentMenu.getId();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", currentMenu.getId());
            jsonBody.put("name", currentMenu.getName() != null ? currentMenu.getName() : "Menu " + currentMenu.getId());
            jsonBody.put("location", locationEdit.getText().toString().trim());
            jsonBody.put("meal", mealTypeEdit.getText().toString().trim());

            String dateStr = dateEdit.getText().toString().trim();
            if (!dateStr.isEmpty()) {
                try {
                    LocalDate.parse(dateStr, dateFormatter);
                    jsonBody.put("date", dateStr);
                } catch (DateTimeParseException e) {
                    Toast.makeText(this, "Invalid date format. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    jsonBody,
                    response -> {
                        Log.d("MenuView", "Menu updated successfully: " + response.toString());
                        Toast.makeText(this, "Menu updated successfully", Toast.LENGTH_SHORT).show();
                        fetchMenuById(currentMenu.getId());
                    },
                    error -> {
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Log.e("MenuView", "Server error response: " + errorResponse);
                                errorMessage = errorResponse;
                            } catch (Exception e) {
                                Log.e("MenuView", "Error parsing error response", e);
                            }
                        }
                        Log.e("MenuView", "Error updating menu: " + errorMessage);
                        Toast.makeText(this, "Error updating menu: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
        Log.e("MenuView", "Error creating update request", e);
        Toast.makeText(this, "Error preparing update request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addFoodItemToMenu(FoodItem foodItem) {
        if (currentMenu == null) return;

        String url = AppConstants.SERVER_URL + "/menu/" + currentMenu.getId() + "/add/" + foodItem.getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                response -> {
                    Log.d("MenuView", "Food item added successfully");
                    Toast.makeText(this, "Food item added to menu", Toast.LENGTH_SHORT).show();

                    fetchMenuById(currentMenu.getId());
                },
                error -> {
                    Log.e("MenuView", "Error adding food item: " + error.getMessage());
                    Toast.makeText(this, "Error adding food item to menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void removeFoodItemFromMenu(FoodItem foodItem) {
        if (currentMenu == null) return;

        String url = AppConstants.SERVER_URL + "/menu/" + currentMenu.getId() + "/remove/" + foodItem.getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                response -> {
                    Log.d("MenuView", "Food item removed successfully");
                    Toast.makeText(this, "Food item removed from menu", Toast.LENGTH_SHORT).show();
                    // Refresh the current menu to show updated food items
                    fetchMenuById(currentMenu.getId());
                },
                error -> {
                    Log.e("MenuView", "Error removing food item: " + error.getMessage());
                    Toast.makeText(this, "Error removing food item from menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchMenuById(int menuId) {
        String url = AppConstants.SERVER_URL + "/menu/" + menuId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        currentMenu = gson.fromJson(response.toString(), Menu.class);
                        updateUIWithMenu(currentMenu);
                    } catch (Exception e) {
                        Log.e("MenuView", "Error parsing menu response", e);
                    }
                },
                error -> {
                    Log.e("MenuView", "Error fetching menu: " + error.getMessage());
                    Toast.makeText(this, "Error refreshing menu data", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void postNewMenuToServer(Menu newMenu) {
        String url = AppConstants.SERVER_URL + "/menu";

        try {
            JSONObject jsonBody = new JSONObject();

            String updatedMealType = newMenu.getMeal().substring(0, 1).toUpperCase() + newMenu.getMeal().substring(1);

            jsonBody.put("name", newMenu.getName());
            jsonBody.put("location", newMenu.getLocation());
            jsonBody.put("meal", updatedMealType);
            jsonBody.put("date", newMenu.getDate());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        Log.d("MenuView", "Menu added successfully: " + response.toString());
                        Toast.makeText(this, "Menu added successfully", Toast.LENGTH_SHORT).show();
                        fetchMenus();
                    },
                    error -> {
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Log.e("MenuView", "Server error response: " + errorResponse);
                                errorMessage = errorResponse;
                            } catch (Exception e) {
                                Log.e("MenuView", "Error parsing error response", e);
                            }
                        }
                        Log.e("MenuView", "Error adding menu: " + errorMessage);
                        Toast.makeText(this, "Error adding menu: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e("MenuView", "Error creating add request", e);
            Toast.makeText(this, "Error preparing add request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteCurrentMenuFromServer() {
        if (currentMenu == null) return;

        String url = AppConstants.SERVER_URL + "/menu/" + currentMenu.getId();

        try {
            StringRequest request = new StringRequest(
                    Request.Method.DELETE,
                    url,
                    response -> {
                        Log.d("MenuView", "Menu deleted successfully: " + response.toString());
                        Toast.makeText(this, "Menu deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchMenus();
                    },
                    error -> {
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Log.e("MenuView", "Server error response: " + errorResponse);
                                errorMessage = errorResponse;
                            } catch (Exception e) {
                                Log.e("MenuView", "Error parsing error response", e);
                            }
                        }
                        Log.e("MenuView", "Error deleting menu: " + errorMessage);
                        Toast.makeText(this, "Error deleting menu: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e("MenuView", "Error creating delete request", e);
            Toast.makeText(this, "Error preparing delete request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}