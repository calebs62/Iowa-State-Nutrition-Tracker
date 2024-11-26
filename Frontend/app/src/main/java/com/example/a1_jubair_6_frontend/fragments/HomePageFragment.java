package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.BaseActivity;
import com.example.a1_jubair_6_frontend.adapters.FoodEatenAdapter;
import com.example.a1_jubair_6_frontend.managers.FoodEatenDataManager;
import com.example.a1_jubair_6_frontend.managers.FoodPlanManager;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodEaten;
import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.utils.FoodEatenPagination;
import com.example.a1_jubair_6_frontend.widgets.NutrientProgressView;
import com.google.android.material.tabs.TabLayout;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePageFragment extends Fragment {

    private NutrientProgressView nutrientProgress;
    private ProfileDataManager profileDataManager;
    private FoodEatenDataManager foodEatenManager;
    private FoodPlanManager foodPlanManager;
    private TabLayout timeRangeTab;
    private Button enter;
    private FoodEatenPagination pagination;
    private Button btnPrevious;
    private Button btnNext;
    private TextView txtPageIndicator;
    private RecyclerView foodEatenRecyclerView;
    private static final String TAG = "HomePageFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagination = new FoodEatenPagination();
        profileDataManager = new ProfileDataManager(requireContext());
        foodEatenManager = new FoodEatenDataManager(requireContext());
        foodPlanManager = new FoodPlanManager(requireContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        nutrientProgress = view.findViewById(R.id.nutrientProgress);
        timeRangeTab = view.findViewById(R.id.timeRangeTab);

        foodEatenRecyclerView = view.findViewById(R.id.foodEatenRecyclerView);
        foodEatenRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        FoodEatenAdapter adapter = new FoodEatenAdapter(foodEaten ->
                foodEatenManager.removeFoodEaten(foodEaten.getId(), new FoodEatenDataManager.FoodEatenCallback() {
                    @Override
                    public void onSuccess() {
                        refreshAllData();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(requireContext(), "Error removing food: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                }));

        foodEatenRecyclerView.setAdapter(adapter);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        txtPageIndicator = view.findViewById(R.id.txtPageIndicator);

        setupPaginationButtons();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFoodEatenList(timeRangeTab.getSelectedTabPosition());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enter = view.findViewById(R.id.btnEnter);

        enter.setOnClickListener(v -> navigateToSubFragment(new GroupFragment()));

        setupTabListener();
        loadFoodPlanAndUpdateProgress();
        updateFoodEatenList(timeRangeTab.getSelectedTabPosition());
    }

    private void setupTabListener() {
        timeRangeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshAllData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshAllData();
            }
        });
    }

    private void updateFoodEatenList(int tabPosition) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = getStartTimeForTab(now, tabPosition);
        LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Date startDate = Date.from(startTime.toInstant(ZoneOffset.UTC));
        Date endDate = Date.from(endTime.toInstant(ZoneOffset.UTC));

        Log.d(TAG, "Fetching food items from " + startDate + " to " + endDate);

        foodEatenManager.getFoodEatenForTimeRange(startDate, endDate,
                new FoodEatenDataManager.FoodEatenListCallback() {
                    @Override
                    public void onSuccess(List<FoodEaten> foodEatenList) {
                        if (getView() == null) return;

                        pagination.setItems(foodEatenList);
                        RecyclerView recyclerView = getView().findViewById(R.id.foodEatenRecyclerView);
                        FoodEatenAdapter adapter = (FoodEatenAdapter) recyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.updateFoodList(pagination.getCurrentPageItems());
                        }
                        updatePaginationUI();
                    }

                    @Override
                    public void onError(String message) {
                        if (getContext() == null) return;
                        Log.e(TAG, "Error loading food items: " + message);
                        Toast.makeText(requireContext(), "Error loading food items: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFoodPlanAndUpdateProgress() {
        Log.d(TAG, "Starting to load food plan");

        foodPlanManager.getAllPlans("", new FoodPlanManager.FoodPlanCallback() {
            @Override
            public void onSuccess(FoodPlan plan) {
                if (plan != null) {
                    Log.d(TAG, "Food plan loaded successfully: " + plan.toString());
                    foodPlanManager.getFoodPlan(plan.getId(), new FoodPlanManager.FoodPlanCallback() {
                        @Override
                        public void onSuccess(FoodPlan detailedPlan) {
                            if (detailedPlan != null) {
                                Log.d(TAG, "Detailed plan loaded: " + detailedPlan.toString());
                                updateNutrientView(new NutrientTotals(), detailedPlan, timeRangeTab.getSelectedTabPosition());
                                updateNutrientProgress(timeRangeTab.getSelectedTabPosition());
                            }
                        }

                        @Override
                        public void onError(String message) {
                            Log.e(TAG, "Error loading detailed plan: " + message);
                        }
                    });
                } else {
                    Log.e(TAG, "No food plan returned from getAllPlans");
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
            Log.e(TAG, "No food plan available in updateNutrientProgress");
            return;
        }

        // Calculate time range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = getStartTimeForTab(now, tabPosition);
        LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Date startDate = Date.from(startTime.toInstant(ZoneOffset.UTC));
        Date endDate = Date.from(endTime.toInstant(ZoneOffset.UTC));

        Log.d(TAG, "Fetching food between " + startDate + " and " + endDate);

        // Fetch food eaten data for the time range
        foodEatenManager.getFoodEatenForTimeRange(startDate, endDate,
                new FoodEatenDataManager.FoodEatenListCallback() {
                    @Override
                    public void onSuccess(List<FoodEaten> foodEatenList) {
                        Log.d(TAG, "Retrieved " + foodEatenList.size() + " food items");
                        Timestamp startTimestamp = new Timestamp(startDate.getTime());
                        NutrientTotals totals = calculateNutrientTotals(foodEatenList, startTimestamp);

                        Log.d(TAG, "Calculated totals: calories=" + totals.calories +
                                ", protein=" + totals.protein +
                                ", carbs=" + totals.carbs +
                                ", fat=" + totals.fat +
                                ", salt=" + totals.salt);

                        updateNutrientView(totals, currentPlan, tabPosition);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, "Error fetching food eaten data: " + message);
                        updateNutrientView(new NutrientTotals(), currentPlan, tabPosition);
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

        Log.d(TAG, "Calculating totals for " + foodEatenList.size() + " food items");

        for (FoodEaten foodEaten : foodEatenList) {
            if (foodEaten.getTime().after(startTime)) {
                float servings = foodEaten.getServings();
                Log.d(TAG, "Processing food: " + foodEaten.getFood().getName() +
                        " servings: " + servings);

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
        // Set the end of day time for better precision
        LocalDateTime start = now.truncatedTo(ChronoUnit.DAYS);

        switch (tabPosition) {
            case 1: // Week
                return start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
            case 2: // Month
                return start.with(TemporalAdjusters.firstDayOfMonth())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
            case 3: // Year
                return start.with(TemporalAdjusters.firstDayOfYear())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
            default: // Today
                return start.withHour(0).withMinute(0).withSecond(0).withNano(0);
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
        updateFoodEatenList(timeRangeTab.getSelectedTabPosition());
    }

    private void updateNutrientView(NutrientTotals totals, FoodPlan plan, int tabPosition) {
        if (plan == null) {
            Log.w(TAG, "No food plan available");
            return;
        }

        float multiplier = getTimeMultiplier(tabPosition);

        Log.d(TAG, "Updating nutrient view with:");
        Log.d(TAG, "Calories - Current: " + (totals != null ? totals.calories : 0) +
                " Goal: " + (plan.getCalories() * multiplier));
        Log.d(TAG, "Protein - Current: " + (totals != null ? totals.protein : 0) +
                " Goal: " + (plan.getProtein() * multiplier));

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

    private void refreshAllData() {
        loadFoodPlanAndUpdateProgress();
        updateFoodEatenList(timeRangeTab.getSelectedTabPosition());
    }

    private void setupPaginationButtons() {
        btnPrevious.setOnClickListener(v -> {
            pagination.previousPage();
            updatePaginationUI();
        });

        btnNext.setOnClickListener(v -> {
            pagination.nextPage();
            updatePaginationUI();
        });
    }

    private void updatePaginationUI() {
        FoodEatenAdapter adapter = (FoodEatenAdapter) foodEatenRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateFoodList(pagination.getCurrentPageItems());
        }

        btnPrevious.setEnabled(pagination.hasPreviousPage());
        btnNext.setEnabled(pagination.hasNextPage());
        txtPageIndicator.setText(String.format("Page %d of %d",
                pagination.getCurrentPage(), pagination.getTotalPages()));
    }

}
