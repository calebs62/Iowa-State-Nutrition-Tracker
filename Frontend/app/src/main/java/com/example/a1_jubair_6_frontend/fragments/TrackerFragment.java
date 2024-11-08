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
import android.view.MenuInflater;
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
import java.util.Objects;

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
    private MaterialButton testFeedButton;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);

        // Initialize views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        filterButton = view.findViewById(R.id.filterButton);
        testFeedButton = view.findViewById(R.id.testFeedButton);

        setupSwipeRefresh();
        setupFilterMenu();
        setupRecyclerView(view);
        setupWebSocket();
        setupTestButton();

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

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            try {
                                Toast.makeText(requireContext(),
                                        "Connection lost. Retrying...",
                                        Toast.LENGTH_SHORT).show();
                                setupWebSocket();
                            } catch (IllegalStateException e) {
                                Log.e("WebSocket", "Fragment not attached", e);
                            }
                        }
                    });
                }
            }
        });

        webSocketClient.connect(wsUrl);
    }

    private boolean shouldShowActivity(ActivityFeedItem.ActivityType type) {
        switch (type) {
            case FOOD_EATEN:
                return profileDataManager.getShowFood();
            case GOAL_UPDATE:
                return profileDataManager.getShowGoals();
            case ACHIEVEMENT:
                return profileDataManager.getShowAchievements();
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

        // Add scroll listener for "Load More" functionality
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if end of list is reached
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    adapter.loadMoreItems();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void refreshActivityFeed() {
        adapter.resetPagination();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void applyFilters() {
        Menu menu = filterMenu.getMenu();
        boolean showFood = menu.findItem(R.id.filter_food).isChecked();
        boolean showGroups = menu.findItem(R.id.filter_groups).isChecked();
        boolean showAchievements = menu.findItem(R.id.filter_achievements).isChecked();
        boolean showGoals = menu.findItem(R.id.filter_goals).isChecked();

        profileDataManager.setShowFood(showFood);
        profileDataManager.setShowGoals(showGoals);
        profileDataManager.setShowAchievements(showAchievements);

        adapter.applyFilters(showFood, showGroups, showAchievements, showGoals);
    }

    private void setupTestButton() {
        testFeedButton = view.findViewById(R.id.testFeedButton);
        if (profileDataManager.isAdminOrContributor()) {
            testFeedButton.setVisibility(View.VISIBLE);
            testFeedButton.setOnClickListener(v -> showTestDialog());
        } else {
            testFeedButton.setVisibility(View.GONE);
        }
    }
    private void showTestDialog() {
        ActivityFeedTestDialog dialog = new ActivityFeedTestDialog();
        dialog.show(getChildFragmentManager(), "activity_test_dialog");
    }
}