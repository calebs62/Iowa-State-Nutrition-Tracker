package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.BaseActivity;
import com.example.a1_jubair_6_frontend.managers.FoodEatenDataManager;
import com.example.a1_jubair_6_frontend.managers.FoodPlanManager;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodEaten;
import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.widgets.NutrientProgressView;
import com.google.android.material.tabs.TabLayout;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private NutrientProgressView nutrientProgress;
    private ProfileDataManager profileDataManager;
    private FoodEatenDataManager foodEatenManager;
    private FoodPlanManager foodPlanManager;
    private TabLayout timeRangeTab;
    private Button enter;
    private static final String TAG = "HomePageFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
        foodEatenManager = new FoodEatenDataManager(requireContext());
        foodPlanManager = new FoodPlanManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        nutrientProgress = view.findViewById(R.id.nutrientProgress);
        timeRangeTab = view.findViewById(R.id.timeRangeTab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enter = view.findViewById(R.id.btnEnter);

        enter.setOnClickListener(v -> navigateToSubFragment(new GroupFragment()));

        setupTabListener();
        loadFoodPlanAndUpdateProgress();
    }

    private void setupTabListener() {
        timeRangeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateNutrientProgress(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFoodPlanAndUpdateProgress() {
        foodPlanManager.getAllPlans("", new FoodPlanManager.FoodPlanCallback() {
            @Override
            public void onSuccess(FoodPlan plan) {
                if (plan != null) {
                    // Initialize with zeros if food data isn't loaded yet
                    updateNutrientView(new NutrientTotals(), plan, timeRangeTab.getSelectedTabPosition());
                    // try to load actual food data
                    updateNutrientProgress(timeRangeTab.getSelectedTabPosition());
                } else {
                    Toast.makeText(requireContext(), "No food plan found. Please set up your goals.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error loading food plan: " + message);
                Toast.makeText(requireContext(), "Error loading food plan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNutrientProgress(int tabPosition) {
        // Get current food plan from the user's group
        FoodPlan currentPlan = foodPlanManager.getCurrentPlan();
        if (currentPlan == null) {
            Log.w(TAG, "No food plan available");
            return;
        }

        // Calculate time range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = getStartTimeForTab(now, tabPosition);

        // Get food eaten data for the time range
        foodEatenManager.getFoodEatenForTimeRange(
                startTime.toInstant(ZoneOffset.UTC).toEpochMilli(),
                now.toInstant(ZoneOffset.UTC).toEpochMilli(),
                new FoodEatenDataManager.FoodEatenListCallback() {
                    @Override
                    public void onSuccess(List<FoodEaten> foodEatenList) {
                        NutrientTotals totals = calculateNutrientTotals(foodEatenList, Timestamp.valueOf(startTime.toString()));
                        updateNutrientView(totals, currentPlan, tabPosition);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, "Error loading food eaten data: " + message);
                        Toast.makeText(requireContext(), "Error loading food data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class NutrientTotals {
        float calories = 0;
        float protein = 0;
        float carbs = 0;
        float fat = 0;
        float salt = 0;
    }

    private NutrientTotals calculateNutrientTotals(List<FoodEaten> foodEatenList, Timestamp startTime) {
        NutrientTotals totals = new NutrientTotals();

        for (FoodEaten foodEaten : foodEatenList) {
            if (foodEaten.getTime().after(startTime)) {
                float servings = foodEaten.getServings();
                totals.calories += foodEaten.getFood().getCalories() * servings;
                totals.protein += foodEaten.getFood().getProtein() * servings;
                totals.carbs += foodEaten.getFood().getCarbohydrate() * servings;
                totals.fat += foodEaten.getFood().getTotalFat() * servings;
                totals.salt += foodEaten.getFood().getSodium() * servings;
            }
        }
        return totals;
    }

    private LocalDateTime getStartTimeForTab(LocalDateTime now, int tabPosition) {
        switch (tabPosition) {
            case 1: // Week
                return now.minus(7, ChronoUnit.DAYS);
            case 2: // Month
                return now.minus(1, ChronoUnit.MONTHS);
            case 3: // Year
                return now.minus(1, ChronoUnit.YEARS);
            default: // Today
                return now.truncatedTo(ChronoUnit.DAYS);
        }
    }

    private float getTimeMultiplier(int tabPosition) {
        switch (tabPosition) {
            case 1: // Week
                return 7f;
            case 2: // Month
                return 30f;
            case 3: // Year
                return 365f;
            default: // Today
                return 1f;
        }
    }

    private void navigateToSubFragment(Fragment fragment) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).loadFragment(fragment, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFoodPlanAndUpdateProgress();
    }

    private void updateNutrientView(NutrientTotals totals, FoodPlan plan, int tabPosition) {
        if (plan == null) {
            Log.w(TAG, "No food plan available");
            return;
        }

        float multiplier = getTimeMultiplier(tabPosition);

        List<NutrientProgressView.NutrientData> nutrientDataList = new ArrayList<>();

        nutrientDataList.add(new NutrientProgressView.NutrientData(
                "Calories",
                totals != null ? totals.calories : 0f,
                plan.getCalories() * multiplier,
                getResources().getColor(R.color.black)));

        nutrientDataList.add(new NutrientProgressView.NutrientData(
                "Protein",
                totals != null ? totals.protein : 0f,
                plan.getProtein() * multiplier,
                getResources().getColor(R.color.Iowa_State_Red)));

        nutrientDataList.add(new NutrientProgressView.NutrientData(
                "Carbs",
                totals != null ? totals.carbs : 0f,
                plan.getCarbohydrate() * multiplier,
                getResources().getColor(R.color.Iowa_State_Gold)));

        nutrientDataList.add(new NutrientProgressView.NutrientData(
                "Fat",
                totals != null ? totals.fat : 0f,
                plan.getTotalFat() * multiplier,
                getResources().getColor(R.color.Iowa_State_Brown)));

        nutrientDataList.add(new NutrientProgressView.NutrientData(
                "Salt",
                totals != null ? totals.salt / 1000 : 0f,
                plan.getSodium() * multiplier / 1000,
                getResources().getColor(R.color.Iowa_State_Light_Brown)));

        nutrientProgress.updateAllNutrients(nutrientDataList);
    }
}
