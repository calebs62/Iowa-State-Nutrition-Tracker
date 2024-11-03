package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.ActivityFeedAdapter;
import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialButton filterButton;
    private PopupMenu filterMenu;
    private ActivityFeedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);

        // Initialize views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        filterButton = view.findViewById(R.id.filterButton);

        setupSwipeRefresh();
        setupFilterMenu();
        setupRecyclerView(view);

        return view;
    }

    private void setupSwipeRefresh() {
        // Set the color scheme for the refresh animation
        swipeRefreshLayout.setColorSchemeResources(
                R.color.Iowa_State_Red,  // Primary color
                R.color.dark_grey        // Secondary color
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh your data here
            refreshActivityFeed();
        });
    }

    private void setupFilterMenu() {
        filterMenu = new PopupMenu(requireContext(), filterButton);
        filterMenu.getMenuInflater().inflate(R.menu.activity_filter_menu, filterMenu.getMenu());

        // Handle filter selection
        filterMenu.setOnMenuItemClickListener(item -> {
            item.setChecked(!item.isChecked()); // Toggle check state
            applyFilters(); // Apply the new filters
            return true;
        });

        filterButton.setOnClickListener(v -> filterMenu.show());
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.activityFeedRecyclerView);
        adapter = new ActivityFeedAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add mock data
        List<ActivityFeedItem> mockData = createMockData();
        adapter.setItems(mockData);
    }

    private void refreshActivityFeed() {
        // TODO: Implement your refresh logic here

        // When the refresh is complete:
        swipeRefreshLayout.setRefreshing(false);
    }

    private void applyFilters() {
        Menu menu = filterMenu.getMenu();
        boolean showFood = menu.findItem(R.id.filter_food).isChecked();
        boolean showGroups = menu.findItem(R.id.filter_groups).isChecked();
        boolean showAchievements = menu.findItem(R.id.filter_achievements).isChecked();
        boolean showGoals = menu.findItem(R.id.filter_goals).isChecked();

        // Apply filters to your adapter
        adapter.applyFilters(showFood, showGroups, showAchievements, showGoals);
    }

    private List<ActivityFeedItem> createMockData() {
        List<ActivityFeedItem> mockItems = new ArrayList<>();

        // Food activities
        ActivityFeedItem foodItem1 = new ActivityFeedItem();
        foodItem1.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
        foodItem1.setMessage("John ate Chicken Caesar Salad");
        foodItem1.setTimestamp(new Timestamp(System.currentTimeMillis() - 3600000)); // 1 hour ago
        foodItem1.setAdditionalData("Calories: 550 • Protein: 35g • Carbs: 20g");
        mockItems.add(foodItem1);

        ActivityFeedItem foodItem2 = new ActivityFeedItem();
        foodItem2.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
        foodItem2.setMessage("Sarah had Grilled Salmon with vegetables");
        foodItem2.setTimestamp(new Timestamp(System.currentTimeMillis() - 7200000)); // 2 hours ago
        foodItem2.setAdditionalData("Calories: 450 • Protein: 42g • Fat: 28g");
        mockItems.add(foodItem2);

        // Group updates
        ActivityFeedItem groupItem1 = new ActivityFeedItem();
        groupItem1.setType(ActivityFeedItem.ActivityType.GROUP_UPDATE);
        groupItem1.setMessage("Fitness Warriors group plan updated");
        groupItem1.setTimestamp(new Timestamp(System.currentTimeMillis() - 10800000)); // 3 hours ago
        groupItem1.setAdditionalData("New meal plan for next week is now available");
        mockItems.add(groupItem1);

        // Achievement (placeholder for future feature)
        ActivityFeedItem achievementItem = new ActivityFeedItem();
        achievementItem.setType(ActivityFeedItem.ActivityType.ACHIEVEMENT);
        achievementItem.setMessage("Mike earned the 'Weekly Goal Crusher' badge!");
        achievementItem.setTimestamp(new Timestamp(System.currentTimeMillis() - 14400000)); // 4 hours ago
        achievementItem.setAdditionalData("Completed all daily goals for 7 days straight");
        mockItems.add(achievementItem);

        // Goal updates
        ActivityFeedItem goalItem = new ActivityFeedItem();
        goalItem.setType(ActivityFeedItem.ActivityType.GOAL_UPDATE);
        goalItem.setMessage("Emma updated her weekly calorie goal");
        goalItem.setTimestamp(new Timestamp(System.currentTimeMillis() - 18000000)); // 5 hours ago
        goalItem.setAdditionalData("New target: 2200 calories per day");
        mockItems.add(goalItem);

        // More food activities
        ActivityFeedItem foodItem3 = new ActivityFeedItem();
        foodItem3.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
        foodItem3.setMessage("Alex logged breakfast");
        foodItem3.setTimestamp(new Timestamp(System.currentTimeMillis() - 21600000)); // 6 hours ago
        foodItem3.setAdditionalData("Oatmeal with berries • Calories: 320");
        mockItems.add(foodItem3);

        // Another group update
        ActivityFeedItem groupItem2 = new ActivityFeedItem();
        groupItem2.setType(ActivityFeedItem.ActivityType.GROUP_UPDATE);
        groupItem2.setMessage("New member joined Healthy Living group");
        groupItem2.setTimestamp(new Timestamp(System.currentTimeMillis() - 25200000)); // 7 hours ago
        groupItem2.setAdditionalData("Welcome David to the group!");
        mockItems.add(groupItem2);

        return mockItems;
    }
}