package com.example.a1_jubair_6_frontend.activities;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.FoodAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.fragments.profile.PasswordAndSecurityFragment;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelFragment extends Fragment {
    private RecyclerView foodList;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;
    private Gson gson = new Gson();
    private View adminToolsContainer;
    private boolean isAdmin = false;
    private boolean isContributor = false;
    private ProfileDataManager profileDataManager;
    private TextView title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());

        String accountType = profileDataManager.getAccountType();
        isAdmin = accountType.equals("ADMINISTRATOR");
        isContributor = accountType.equals("CONTRIBUTOR");

        foodItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        if(isContributor) {
            title = view.findViewById(R.id.tvAdminPanel);
            title.setText("Contributor Panel");
        }

        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            // Pop the back stack instead of creating a new fragment
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        foodList = view.findViewById(R.id.foodList);
        foodList.setLayoutManager(new LinearLayoutManager(requireContext()));

        foodAdapter = new FoodAdapter(foodItemList, isAdmin, isContributor,false);
        foodList.setAdapter(foodAdapter);

        int bottomNavHeight = getResources().getDimensionPixelSize(R.dimen.bottom_nav_height);
        foodList.setPadding(0, 0, 0, bottomNavHeight);

        Button btnAddFood = view.findViewById(R.id.btnAddFood);
        if (btnAddFood != null) {
            btnAddFood.setOnClickListener(v -> showAddDialog());
        }

        getAllFoodItems();
    }

    private void getAllFoodItems() {
        String url = AppConstants.SERVER_URL + "/item";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.PUT,
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


    public void goBack(){
        Fragment adminPanelFragment = new AdminPanelFragment();

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_left)
                .replace(R.id.container, adminPanelFragment)
                .addToBackStack(null)
                .commit();
    }
}