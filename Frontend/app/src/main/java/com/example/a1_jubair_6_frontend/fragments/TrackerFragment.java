package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.ActivityFeedAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class TrackerFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialButton filterButton;
    private PopupMenu filterMenu;
    private ActivityFeedAdapter adapter;
    private WebSocketClient webSocketClient;
    private ProfileDataManager profileDataManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

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
        setupWebSocket();

        return view;
    }

    private void setupWebSocket() {
        int userId = profileDataManager.getId();

        if (userId == -1) {
            Log.e("WebSocket", "User ID not found");
            Toast.makeText(requireContext(), "Please log in to view activity feed", Toast.LENGTH_LONG).show();
            return;
        }

        String wsUrl = AppConstants.WEBSOCKET_SERVER_URL + userId;
        Log.d("WebSocket", "Connecting to: " + wsUrl);

        webSocketClient = new WebSocketClient(new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                Log.d("WebSocket", "Connection established for user: " + userId);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Connected to activity feed",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                Log.d("WebSocket", "Received message: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    ActivityFeedItem item = new ActivityFeedItem();
                    item.setType(ActivityFeedItem.ActivityType.valueOf(json.getString("type")));
                    item.setMessage(json.getString("message"));
                    item.setTimestamp(Timestamp.valueOf(json.getString("timestamp")));
                    item.setAdditionalData(json.getString("additionalData"));

                    Log.d("WebSocket", "Parsed message into ActivityFeedItem");

                    boolean shouldShow = shouldShowActivity(item.getType());
                    Log.d("WebSocket", "Should show item: " + shouldShow);

                    if (shouldShow) {
                        requireActivity().runOnUiThread(() -> {
                            adapter.addItem(item);
                            adapter.notifyDataSetChanged();
                            Log.d("WebSocket", "Added item to adapter");
                        });
                    }
                } catch (JSONException e) {
                    Log.e("WebSocket", "Error parsing message: " + e.getMessage());
                    e.printStackTrace();
                }
            }


            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                Log.e("WebSocket", "Closing with code: " + code + ", reason: " + reason);
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                Log.d("WebSocket", "Closed: " + reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                Log.e("WebSocket", "Error: " + t.getMessage(), t);
                t.printStackTrace();
                String responseBody = response != null ? response.toString() : "No response";
                Log.e("WebSocket", "Response: " + responseBody);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Connection lost. Retrying...",
                            Toast.LENGTH_SHORT).show();
                    setupWebSocket();
                });
            }
        });

        webSocketClient.connect(wsUrl);
    }

    private boolean shouldShowActivity(ActivityFeedItem.ActivityType type) {
        switch (type) {
            case FOOD_EATEN:
                return profileDataManager.getFoodSharingEnabled();
            case GOAL_UPDATE:
                return profileDataManager.getGoalSharingEnabled();
            case ACHIEVEMENT:
                return profileDataManager.getAchievementSharingEnabled();
            case GROUP_UPDATE:
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }


    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.Iowa_State_Red,
                R.color.dark_grey
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshActivityFeed();
        });
    }

    private void setupFilterMenu() {
        filterMenu = new PopupMenu(requireContext(), filterButton);
        filterMenu.getMenuInflater().inflate(R.menu.activity_filter_menu, filterMenu.getMenu());

        // Initialize menu items based on privacy settings
        Menu menu = filterMenu.getMenu();
        menu.findItem(R.id.filter_food).setChecked(profileDataManager.getFoodSharingEnabled());
        menu.findItem(R.id.filter_goals).setChecked(profileDataManager.getGoalSharingEnabled());
        menu.findItem(R.id.filter_achievements).setChecked(profileDataManager.getAchievementSharingEnabled());

        filterMenu.setOnMenuItemClickListener(item -> {
            item.setChecked(!item.isChecked());
            applyFilters();
            return true;
        });

        filterButton.setOnClickListener(v -> filterMenu.show());
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.activityFeedRecyclerView);
        adapter = new ActivityFeedAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

//        List<ActivityFeedItem> mockData = createMockData();
//        adapter.setItems(mockData);
    }

    private void refreshActivityFeed() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void applyFilters() {
        Menu menu = filterMenu.getMenu();
        boolean showFood = menu.findItem(R.id.filter_food).isChecked();
        boolean showGroups = menu.findItem(R.id.filter_groups).isChecked();
        boolean showAchievements = menu.findItem(R.id.filter_achievements).isChecked();
        boolean showGoals = menu.findItem(R.id.filter_goals).isChecked();

        adapter.applyFilters(showFood, showGroups, showAchievements, showGoals);
    }

//    private List<ActivityFeedItem> createMockData() {
//        List<ActivityFeedItem> mockItems = new ArrayList<>();
//
//        // Food activities
//        ActivityFeedItem foodItem1 = new ActivityFeedItem();
//        foodItem1.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
//        foodItem1.setMessage("John ate Chicken Caesar Salad");
//        foodItem1.setTimestamp(new Timestamp(System.currentTimeMillis() - 3600000)); // 1 hour ago
//        foodItem1.setAdditionalData("Calories: 550 • Protein: 35g • Carbs: 20g");
//        mockItems.add(foodItem1);
//
//        ActivityFeedItem foodItem2 = new ActivityFeedItem();
//        foodItem2.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
//        foodItem2.setMessage("Sarah had Grilled Salmon with vegetables");
//        foodItem2.setTimestamp(new Timestamp(System.currentTimeMillis() - 7200000)); // 2 hours ago
//        foodItem2.setAdditionalData("Calories: 450 • Protein: 42g • Fat: 28g");
//        mockItems.add(foodItem2);
//
//        // Group updates
//        ActivityFeedItem groupItem1 = new ActivityFeedItem();
//        groupItem1.setType(ActivityFeedItem.ActivityType.GROUP_UPDATE);
//        groupItem1.setMessage("Fitness Warriors group plan updated");
//        groupItem1.setTimestamp(new Timestamp(System.currentTimeMillis() - 10800000)); // 3 hours ago
//        groupItem1.setAdditionalData("New meal plan for next week is now available");
//        mockItems.add(groupItem1);
//
//        // Achievement (placeholder for future feature)
//        ActivityFeedItem achievementItem = new ActivityFeedItem();
//        achievementItem.setType(ActivityFeedItem.ActivityType.ACHIEVEMENT);
//        achievementItem.setMessage("Mike earned the 'Weekly Goal Crusher' badge!");
//        achievementItem.setTimestamp(new Timestamp(System.currentTimeMillis() - 14400000)); // 4 hours ago
//        achievementItem.setAdditionalData("Completed all daily goals for 7 days straight");
//        mockItems.add(achievementItem);
//
//        // Goal updates
//        ActivityFeedItem goalItem = new ActivityFeedItem();
//        goalItem.setType(ActivityFeedItem.ActivityType.GOAL_UPDATE);
//        goalItem.setMessage("Emma updated her weekly calorie goal");
//        goalItem.setTimestamp(new Timestamp(System.currentTimeMillis() - 18000000)); // 5 hours ago
//        goalItem.setAdditionalData("New target: 2200 calories per day");
//        mockItems.add(goalItem);
//
//        // More food activities
//        ActivityFeedItem foodItem3 = new ActivityFeedItem();
//        foodItem3.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
//        foodItem3.setMessage("Alex logged breakfast");
//        foodItem3.setTimestamp(new Timestamp(System.currentTimeMillis() - 21600000)); // 6 hours ago
//        foodItem3.setAdditionalData("Oatmeal with berries • Calories: 320");
//        mockItems.add(foodItem3);
//
//        // Another group update
//        ActivityFeedItem groupItem2 = new ActivityFeedItem();
//        groupItem2.setType(ActivityFeedItem.ActivityType.GROUP_UPDATE);
//        groupItem2.setMessage("New member joined Healthy Living group");
//        groupItem2.setTimestamp(new Timestamp(System.currentTimeMillis() - 25200000)); // 7 hours ago
//        groupItem2.setAdditionalData("Welcome David to the group!");
//        mockItems.add(groupItem2);
//
//        return mockItems;
//    }
}