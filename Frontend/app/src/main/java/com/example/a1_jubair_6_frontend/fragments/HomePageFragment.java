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

import com.airbnb.lottie.LottieAnimationView;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomePageFragment extends Fragment implements GroupFragment.GroupMembershipCallback {

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
    private View emptyStateContainer;
    private View nutrientProgressContainer;
    private View foodEatenContainer;
    private View groupInformationContainer;
    Button btnViewGroups;
    private static final String TAG = "HomePageFragment";
    private LottieAnimationView loadingAnimation;

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

        loadingAnimation = view.findViewById(R.id.loadingAnimation);

        foodEatenRecyclerView = view.findViewById(R.id.foodEatenRecyclerView);
        foodEatenRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        emptyStateContainer = view.findViewById(R.id.emptyStateContainer);
        nutrientProgressContainer = view.findViewById(R.id.nutrientProgressContainer);
        foodEatenContainer = view.findViewById(R.id.foodEatenContainer);
        groupInformationContainer = view.findViewById(R.id.groupInformationContainer);

        btnViewGroups = view.findViewById(R.id.btnViewGroups);
        btnViewGroups.setOnClickListener(v -> {
            GroupFragment groupFragment = new GroupFragment();
            groupFragment.setGroupMembershipCallback(this);
            navigateToSubFragment(groupFragment);
        });

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enter = view.findViewById(R.id.btnEnter);
        enter.setOnClickListener(v -> {
            GroupFragment groupFragment = new GroupFragment();
            groupFragment.setGroupMembershipCallback(this);
            navigateToSubFragment(groupFragment);
        });
        setupTabListener();
        showLoading();
        loadAllData();
    }

    private void updateUIState() {
        if (!canUpdateUI()) {
            Log.d(TAG, "Cannot update UI - fragment not in valid state");
            return;
        }

        boolean hasPlan = foodPlanManager.getCurrentPlan() != null;
        Log.d(TAG, "Updating UI state. Has plan: " + hasPlan);

        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.GONE);
        }

        if (emptyStateContainer != null) {
            emptyStateContainer.setVisibility(hasPlan ? View.GONE : View.VISIBLE);
            Log.d(TAG, "Empty state container visibility: " +
                    (hasPlan ? "GONE" : "VISIBLE"));
        }

        if (nutrientProgressContainer != null) {
            nutrientProgressContainer.setVisibility(hasPlan ? View.VISIBLE : View.GONE);
        }

        if (foodEatenContainer != null) {
            foodEatenContainer.setVisibility(hasPlan ? View.VISIBLE : View.GONE);
        }

        if (groupInformationContainer != null) {
            groupInformationContainer.setVisibility(hasPlan ? View.VISIBLE : View.GONE);
        }

        if (btnViewGroups != null) {
            btnViewGroups.setVisibility(View.VISIBLE);
        }

        if (timeRangeTab != null) {
            timeRangeTab.setEnabled(hasPlan);
            timeRangeTab.setAlpha(hasPlan ? 1.0f : 0.5f);
        }
    }

    private void setupTabListener() {
        timeRangeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshAllData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshAllData();
            }
        });
    }

    private void updateFoodEatenList(int tabPosition) {
        if (!canUpdateUI()) return;

        ZoneOffset systemOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = getStartTimeForTab(now, tabPosition);
        LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Date startDate = Date.from(startTime.toInstant(systemOffset));
        Date endDate = Date.from(endTime.toInstant(systemOffset));

        Log.d(TAG, "Fetching food items from " + startDate + " to " + endDate);

        foodEatenManager.getFoodEatenForTimeRange(startDate, endDate,
                new FoodEatenDataManager.FoodEatenListCallback() {
                    @Override
                    public void onSuccess(List<FoodEaten> foodEatenList) {
                        if (!canUpdateUI()) return;

                        List<FoodEaten> filteredList = new ArrayList<>();
                        for (FoodEaten food : foodEatenList) {
                            if (food.getTime() != null) {
                                if (!food.getTime().before(startDate) && !food.getTime().after(endDate)) {
                                    filteredList.add(food);
                                    Log.d(TAG, String.format("Including food item: %s with server date: %s",
                                            food.getFood().getName(),
                                            food.getTime()));
                                } else {
                                    Log.d(TAG, String.format("Excluding food item: %s with server date: %s",
                                            food.getFood().getName(),
                                            food.getTime()));
                                }
                            }
                        }

                        Collections.sort(filteredList, (a, b) -> b.getTime().compareTo(a.getTime()));

                        Log.d(TAG, String.format("Filtered list contains %d items", filteredList.size()));

                        pagination.setItems(filteredList);
                        RecyclerView recyclerView = getView().findViewById(R.id.foodEatenRecyclerView);
                        FoodEatenAdapter adapter = (FoodEatenAdapter) recyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.updateFoodList(pagination.getCurrentPageItems());
                        }
                        updatePaginationUI();
                    }

                    @Override
                    public void onError(String message) {
                        if (!canUpdateUI()) return;
                        Log.e(TAG, "Error loading food items: " + message);
                        Toast.makeText(requireContext(), "Error loading food items: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFoodPlanAndUpdateProgress() {
        Log.d(TAG, "Starting to load food plan");

        if (foodPlanManager.getCurrentPlan() != null) {
            Log.i(TAG, "Food Plan already grabbed");
            updateNutrientProgress(timeRangeTab.getSelectedTabPosition());
            return;
        }

        foodPlanManager.getFoodPlanFromGroup(new FoodPlanManager.FoodPlanCallback() {
            @Override
            public void onSuccess(FoodPlan plan) {
                if (plan != null) {
                    Log.d(TAG, "Food plan loaded successfully: " + plan.toString());
                    updateNutrientProgress(timeRangeTab.getSelectedTabPosition());
                } else {
                    Log.e(TAG, "No food plan returned from getAllPlans");
                    Toast.makeText(requireContext(), "No food plan found. Please join a group to get a food plan.", Toast.LENGTH_LONG).show();
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
        if (!canUpdateUI()) return;
        // Get current food plan from the user's group
        FoodPlan currentPlan = foodPlanManager.getCurrentPlan();
        if (currentPlan == null) {
            Log.e(TAG, "No food plan available in updateNutrientProgress");
            return;
        }

        ZoneOffset systemOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        // Calculate time range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = getStartTimeForTab(now, tabPosition);
        LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Date startDate = Date.from(startTime.toInstant(systemOffset));
        Date endDate = Date.from(endTime.toInstant(systemOffset));

        Log.d(TAG, "Fetching food between " + startDate + " and " + endDate);

        foodEatenManager.getFoodEatenForTimeRange(startDate, endDate,
                new FoodEatenDataManager.FoodEatenListCallback() {
                    @Override
                    public void onSuccess(List<FoodEaten> foodEatenList) {
                        if (!canUpdateUI()) return;

                        Log.d(TAG, "Retrieved " + foodEatenList.size() + " food items");
                        for (FoodEaten food : foodEatenList) {
                            Log.d(TAG, "Food item date: " + food.getTime() + " - " + food.getFood().getName());
                        }

                        Timestamp startTimestamp = new Timestamp(startDate.getTime());
                        NutrientTotals totals = calculateNutrientTotals(foodEatenList, startTimestamp);
                        updateNutrientView(totals, currentPlan, tabPosition);
                    }

                    @Override
                    public void onError(String message) {
                        if (!canUpdateUI()) return;
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
        ZoneOffset systemOffset = ZoneOffset.systemDefault().getRules().getOffset(now);

        LocalDateTime start = now.truncatedTo(ChronoUnit.DAYS);

        LocalDateTime result;
        switch (tabPosition) {
            case 1: // Week
                result = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                break;
            case 2: // Month
                result = start.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case 3: // Year
                result = start.with(TemporalAdjusters.firstDayOfYear());
                break;
            default: // Today
                result = start;
                break;
        }

        Date startDate = Date.from(result.toInstant(systemOffset));
        Date endDate = Date.from(now.withHour(23).withMinute(59).withSecond(59)
                .withNano(999999999).toInstant(systemOffset));
        Log.d(TAG, String.format("Date range - Start: %s (timestamp: %d)",
                startDate, startDate.getTime()));
        Log.d(TAG, String.format("            End: %s (timestamp: %d)",
                endDate, endDate.getTime()));

        return result;
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
        if (foodPlanManager.getCurrentPlan() == null) {
            loadAllData();
        }
    }

    private void updateNutrientView(NutrientTotals totals, FoodPlan plan, int tabPosition) {
        if (!canUpdateUI() || plan == null) {
            Log.w(TAG, "Cannot update nutrient view - fragment not attached or no plan available");
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

        if (nutrientProgress != null) {
            nutrientProgress.updateAllNutrients(nutrientDataList);
        }
    }

    private void refreshAllData() {
        loadAllData();
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

    @Override
    public void onGroupMembershipChanged() {
        Log.d(TAG, "Group membership changed called");

        if (!canUpdateUI()) {
            Log.d(TAG, "Skip membership change - fragment not in valid state");
            return;
        }

        if (getActivity() == null) {
            Log.d(TAG, "Skip membership change - activity is null");
            return;
        }

        getActivity().runOnUiThread(() -> {
            foodPlanManager.clearCurrentPlan();
            Log.d(TAG, "Current plan cleared");

            updateUIState();
            Log.d(TAG, "UI State updated");

            new android.os.Handler().postDelayed(() -> {
                if (canUpdateUI()) {
                    loadAllData();
                    Log.d(TAG, "Data reload initiated");
                }
            }, 150);
        });
    }

    private boolean canUpdateUI() {
        return isAdded() && !isDetached() && getContext() != null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nutrientProgress = null;
    }


    private void loadAllData() {
        if (!canUpdateUI()) {
            Log.d(TAG, "Cannot load data - fragment not in valid state");
            return;
        }

        Log.d(TAG, "Starting loadAllData()");
        showLoading();

        Log.d(TAG, "Requesting food plan from FoodPlanManager");
        foodPlanManager.getFoodPlanFromGroup(new FoodPlanManager.FoodPlanCallback() {
            @Override
            public void onSuccess(FoodPlan plan) {
                Log.d(TAG, "FoodPlanManager callback received - success. Plan: " + (plan != null ? "found" : "null"));

                if (!canUpdateUI()) {
                    Log.d(TAG, "Cannot update UI in callback - fragment not in valid state");
                    return;
                }

                hideLoading();

                if (plan == null) {
                    Log.d(TAG, "No food plan found - showing empty state");
                    updateUIState();
                } else {
                    Log.d(TAG, "Food plan found - loading data");
                    updateNutrientProgress(timeRangeTab.getSelectedTabPosition());
                    updateFoodEatenList(timeRangeTab.getSelectedTabPosition());
                    updateUIState();
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "FoodPlanManager callback received - error: " + message);

                if (!canUpdateUI()) {
                    Log.d(TAG, "Cannot update UI in error callback - fragment not in valid state");
                    return;
                }

                hideLoading();
                updateUIState();
                Toast.makeText(requireContext(),
                        "Unable to load food plan: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.VISIBLE);
            loadingAnimation.setAnimation("loading.json");
            loadingAnimation.loop(true);
            loadingAnimation.playAnimation();
        }

        if (emptyStateContainer != null) emptyStateContainer.setVisibility(View.GONE);
        if (nutrientProgressContainer != null) nutrientProgressContainer.setVisibility(View.GONE);
        if (foodEatenContainer != null) foodEatenContainer.setVisibility(View.GONE);
        if (groupInformationContainer != null) groupInformationContainer.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.GONE);
            loadingAnimation.pauseAnimation();
        }
    }
}
