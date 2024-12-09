package com.example.a1_jubair_6_frontend.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.adapters.ActivityFeedAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private static final int PICK_IMAGE_REQUEST = 1;
    private String currentEncodedImage;
    private MaterialButton uploadImageButton;


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
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(v -> openImagePicker());

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

                    String typeStr = json.getString("type");
                    try {
                        item.setType(ActivityFeedItem.ActivityType.valueOf(typeStr));
                    } catch (IllegalArgumentException e) {
                        item.setType(ActivityFeedItem.ActivityType.GROUP_UPDATE);
                    }

                    item.setMessage(json.getString("message"));
                    item.setTimestamp(Timestamp.valueOf(json.getString("timestamp")));
                    item.setAdditionalData(json.getString("additionalData"));

                    if (json.has("images")) {
                        JSONArray imagesArray = json.getJSONArray("images");
                        Log.d("WebSocket", "Message contains " + imagesArray.length() + " images");
                        List<String> images = new ArrayList<>();
                        for (int i = 0; i < imagesArray.length(); i++) {
                            images.add(imagesArray.getString(i));
                            Log.d("WebSocket", "Added image of length: " + imagesArray.getString(i).length());
                        }
                        item.setImages(images);
                    }

                    Log.d("WebSocket", "Parsed message into ActivityFeedItem");

                    boolean shouldShow = shouldShowActivity(item.getType());
                    Log.d("WebSocket", "Should show item: " + shouldShow);

                    if (shouldShow && isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            adapter.addItem(item);
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

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                int maxDimension = Math.max(bitmap.getWidth(), bitmap.getHeight());
                if (maxDimension > 1024) {
                    float scale = 1024f / maxDimension;
                    bitmap = Bitmap.createScaledBitmap(bitmap,
                            (int) (bitmap.getWidth() * scale),
                            (int) (bitmap.getHeight() * scale),
                            true);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                currentEncodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                uploadImage(currentEncodedImage);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(String base64Image) {
        if (base64Image.length() > 5_000_000) {
            Toast.makeText(requireContext(),
                    "Image is too large. Please choose a smaller image.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String url = AppConstants.SERVER_URL + "/activity/image/" +
                profileDataManager.getId() + "/" +
                profileDataManager.getGroupId();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("image", base64Image);
            requestBody.put("caption", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("Upload", "Image uploaded successfully");
                    Toast.makeText(requireContext(),
                            "Image shared successfully",
                            Toast.LENGTH_SHORT).show();
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e("Upload", "Error response: " + error.toString());
                    if (error.networkResponse == null || error.networkResponse.statusCode != 200) {
                        String errorMessage = "Failed to share image";
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 413:
                                    errorMessage = "Image is too large";
                                    break;
                                case 401:
                                    errorMessage = "Please log in again";
                                    break;
                                case 403:
                                    errorMessage = "You don't have permission to share images";
                                    break;
                            }
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            Log.e("Upload Error", "Status Code: " + error.networkResponse.statusCode +
                                    " Error: " + error.toString());
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    return requestBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(requireContext())
                .addToRequestQueue(request);
    }
}