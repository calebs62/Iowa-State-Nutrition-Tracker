package com.example.a1_jubair_6_frontend.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.*;
import com.example.a1_jubair_6_frontend.adapters.FoodAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuFragment extends Fragment {
    private RecyclerView foodList;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;
    private Gson gson = new Gson();
    private View adminToolsContainer;
    private boolean isAdmin = false;
    ProfileDataManager profileDataManager;

    int currentBreakfastMenuIndex, currentLunchMenuIndex, currentDinnerMenuIndex;
    private String mealType;
    private String menuLocation;
    private Timestamp menuDate;
    List<Menu> allMenus, breakfastMenus, lunchMenus, dinnerMenus;
    TabLayout mealTypeTabs;

    MaterialButton addButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foodItemList = new ArrayList<>();
        profileDataManager = new ProfileDataManager(requireContext());
        String accountType = profileDataManager.getAccountType();
        isAdmin = accountType.equals("ADMINISTRATOR") || accountType.equals("CONTRIBUTOR");
        allMenus = new ArrayList<>();
        breakfastMenus = new ArrayList<>();
        lunchMenus = new ArrayList<>();
        dinnerMenus = new ArrayList<>();
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

        adminToolsContainer = view.findViewById(R.id.adminToolsContainer);
        if (isAdmin) {
            adminToolsContainer.setVisibility(View.VISIBLE);
        }

        foodList = view.findViewById(R.id.foodList);
        foodList.setLayoutManager(new LinearLayoutManager(requireContext()));

        foodAdapter = new FoodAdapter(foodItemList, isAdmin);
        foodList.setAdapter(foodAdapter);

        int bottomNavHeight = getResources().getDimensionPixelSize(R.dimen.bottom_nav_height);
        foodList.setPadding(0, 0, 0, bottomNavHeight);

        //Gets menus from server
        //getAllMenus();

        mealTypeTabs = view.findViewById(R.id.mealTypeTabs);

        //For Testing purposes, need to remove once we havefull communication with server:
        Menu mockBreakfastMenu = createMockBreakfastMenu();
        Menu mockLunchMenu = createMockLunchMenu();
        Menu mockDinnerMenu = createMockDinnerMenu();
        breakfastMenus.add(mockBreakfastMenu);
        lunchMenus.add(mockLunchMenu);
        dinnerMenus.add(mockDinnerMenu);

        //TODO: Need to set these to what the admin wants, ex. Have a dialog show the list of menus and once you select a certain one it will set this index (hidden controls for admin to add,edit,delete menus)
        currentBreakfastMenuIndex = 0;
        currentLunchMenuIndex = 0;
        currentDinnerMenuIndex = 0;

        //This makes sure the breakfast tab isn't blank on created
        foodItemList.clear();
        foodItemList.addAll(breakfastMenus.get(currentBreakfastMenuIndex).getFoodItems());
        foodAdapter.notifyDataSetChanged();

        //Sets each tab with their corresponding menu set by the admin
        updateTab();

        Button viewMenus = view.findViewById(R.id.btnViewMenus);
        viewMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewMenusActivityTesting.class);

                // Serialize the mock menu into a JSON string and pass it as an extra
                Menu mockBreakfastMenu = createMockBreakfastMenu(); // Get your mock menu
                String menuJson = gson.toJson(mockBreakfastMenu);

                intent.putExtra("menu_data", menuJson); // Pass the menu data to the next activity
                startActivity(intent);
            }
        });

    }

    // <editor-fold desc="HTTP Requests">

    private void getAllMenus() {
        String url = AppConstants.SERVER_URL + "/allMenus";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    for (int i = 0; i < response.length(); i++){
                        try{
                            JSONObject menuJson = response.getJSONObject(i);
                            Menu menu = gson.fromJson(menuJson.toString(), Menu.class);
                            allMenus.add(menu);

                            Log.i("All Menus Request", "Got all menus from server");
                        } catch (JSONException e) {
                            Log.e("Response Error", String.valueOf(e.getMessage()));
                        }
                    }
                },
                error -> {
                    Log.e("Request Error", "Could not get all menus from server");
                }
        );
        sortMenus();
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void getMenuFromId(int menuId) {
        String url = AppConstants.SERVER_URL + "/menu/" + menuId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Menu menu = gson.fromJson(response.toString(), Menu.class);

                    mealType = menu.getMeal();
                    menuDate = menu.getDate();
                    menuLocation = menu.getLocation();

                    foodItemList.clear();
                    foodItemList.addAll(menu.getFoodItems());

                    foodAdapter.notifyDataSetChanged();

                    Log.i("MenuFragment", "Got menu from id " + menuId + ". Menu: [" + menu + "]");
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

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
                    Toast.makeText(requireContext(), "Successfully created food item", Toast.LENGTH_SHORT).show();
                    FoodItem createdItem = gson.fromJson(response.toString(), FoodItem.class);
                    foodItemList.add(createdItem);
                    foodAdapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(requireContext(), "Error creating food item [" + error.networkResponse.statusCode + "]", Toast.LENGTH_SHORT);
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

    // </editor-fold>

    // <editor-fold desc="Helper Methods">

    private void sortMenus(){
        for(Menu menu : allMenus){
            if(menu.getMeal().equalsIgnoreCase("breakfast")){
                breakfastMenus.add(menu);
            }
            else if(menu.getMeal().equalsIgnoreCase("lunch")){
                lunchMenus.add(menu);
            }
            else if(menu.getMeal().equalsIgnoreCase("dinner")){
                dinnerMenus.add(menu);
            }
        }
    }

    private void updateItemInList(FoodItem updatedItem) {
        for (int i = 0; i < foodItemList.size(); i++) {
            if (foodItemList.get(i).getId() == updatedItem.getId()) {
                foodItemList.set(i, updatedItem);
                foodAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.food_item_dialog, null);

        TextInputEditText nameInput = dialogView.findViewById(R.id.editTextName);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.editTextDescription);
        TextInputEditText servingSizeInput = dialogView.findViewById(R.id.editTextServingSize);
        TextInputEditText caloriesInput = dialogView.findViewById(R.id.editTextCalories);
        TextInputEditText totalFatInput = dialogView.findViewById(R.id.editTextTotalFat);
        TextInputEditText sodiumInput = dialogView.findViewById(R.id.editTextSodium);
        TextInputEditText carbohydrateInput = dialogView.findViewById(R.id.editTextCarbohydrate);
        TextInputEditText proteinInput = dialogView.findViewById(R.id.editTextProtein);

        AlertDialog dialog = builder
                .setView(dialogView)
                .setTitle("Add Food Item")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                try {
                    FoodItem newItem = new FoodItem(
                            nameInput.getText().toString(),
                            Integer.parseInt(caloriesInput.getText().toString()),
                            Integer.parseInt(totalFatInput.getText().toString()),
                            Integer.parseInt(sodiumInput.getText().toString()),
                            Integer.parseInt(carbohydrateInput.getText().toString()),
                            Integer.parseInt(proteinInput.getText().toString()),
                            servingSizeInput.getText().toString(),
                            descriptionInput.getText().toString()
                    );

                    createFoodItem(newItem);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Please fill all numeric fields correctly", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(requireContext(), "Error creating food item", Toast.LENGTH_SHORT).show();
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

    private void updateTab(){
        mealTypeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // Breakfast tab
                        foodItemList.clear();
                        foodItemList.addAll(breakfastMenus.get(currentBreakfastMenuIndex).getFoodItems());
                        foodAdapter.notifyDataSetChanged();
                        break;
                    case 1: // Lunch tab
                        foodItemList.clear();
                        foodItemList.addAll(lunchMenus.get(currentLunchMenuIndex).getFoodItems());
                        foodAdapter.notifyDataSetChanged();
                        break;
                    case 2: // Dinner tab
                        foodItemList.clear();
                        foodItemList.addAll(dinnerMenus.get(currentDinnerMenuIndex).getFoodItems());
                        foodAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    //</editor-fold>

    public static Menu createMockBreakfastMenu() {
        // Create mock food items
        Set<FoodItem> breakfastItems = new HashSet<>();
        breakfastItems.add(new FoodItem("Pancakes", 350, 10, 600, 60, 8, "2 pancakes", "Fluffy pancakes with syrup"));
        breakfastItems.add(new FoodItem("Scrambled Eggs", 200, 15, 150, 2, 12, "1 serving", "Lightly scrambled eggs"));
        breakfastItems.add(new FoodItem("Orange Juice", 110, 0, 5, 25, 2, "8 oz", "Freshly squeezed orange juice"));

        // Create mock breakfast menu
        Menu breakfastMenu = new Menu("Main Dining Hall", "Breakfast", new Timestamp(System.currentTimeMillis()));
        breakfastMenu.setFoodItems(breakfastItems);
        return breakfastMenu;
    }

    public static Menu createMockLunchMenu() {
        // Create mock food items
        Set<FoodItem> lunchItems = new HashSet<>();
        lunchItems.add(new FoodItem("Grilled Chicken Sandwich", 500, 20, 900, 45, 30, "1 sandwich", "Grilled chicken with lettuce and tomato on a bun"));
        lunchItems.add(new FoodItem("Caesar Salad", 350, 25, 600, 10, 8, "1 bowl", "Classic Caesar salad with croutons"));
        lunchItems.add(new FoodItem("Iced Tea", 50, 0, 10, 12, 0, "12 oz", "Unsweetened iced tea"));

        // Create mock lunch menu
        Menu lunchMenu = new Menu("Main Dining Hall", "Lunch", new Timestamp(System.currentTimeMillis()));
        lunchMenu.setFoodItems(lunchItems);
        return lunchMenu;
    }

    public static Menu createMockDinnerMenu() {
        // Create mock food items
        Set<FoodItem> dinnerItems = new HashSet<>();
        dinnerItems.add(new FoodItem("Steak", 700, 40, 1200, 0, 60, "8 oz", "Grilled ribeye steak"));
        dinnerItems.add(new FoodItem("Mashed Potatoes", 250, 10, 500, 35, 5, "1 cup", "Creamy mashed potatoes"));
        dinnerItems.add(new FoodItem("Red Wine", 125, 0, 5, 4, 0, "5 oz", "Glass of red wine"));

        // Create mock dinner menu
        Menu dinnerMenu = new Menu("Main Dining Hall", "Dinner", new Timestamp(System.currentTimeMillis()));
        dinnerMenu.setFoodItems(dinnerItems);
        return dinnerMenu;
    }
}