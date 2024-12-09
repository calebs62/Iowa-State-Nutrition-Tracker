package com.example.a1_jubair_6_frontend.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityFeedAdapter extends RecyclerView.Adapter<ActivityFeedAdapter.ViewHolder> {
    private static final int ITEMS_PER_PAGE = 10;
    private List<ActivityFeedItem> allActivityItems = new ArrayList<>();
    private List<ActivityFeedItem> filteredActivityItems = new ArrayList<>();
    private List<ActivityFeedItem> displayedItems = new ArrayList<>();
    private Context context;
    private boolean showFood = true;
    private boolean showGroups = true;
    private boolean showAchievements = true;
    private boolean showGoals = true;
    private int currentPage = 0;
    private boolean isLoading = false;

    private OnLoadMoreListener loadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public ActivityFeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityFeedItem item = displayedItems.get(position);
        holder.bind(item);

        // Use post to avoid layout/scroll conflicts
        if (!isLoading && position >= displayedItems.size() - 3 &&
                displayedItems.size() < filteredActivityItems.size()) {
            new Handler(Looper.getMainLooper()).post(this::loadMoreItems);
        }
    }

    @Override
    public int getItemCount() {
        return displayedItems.size();
    }

    public void addItem(ActivityFeedItem item) {
        boolean isDuplicate = allActivityItems.stream()
                .anyMatch(existingItem ->
                        existingItem.getMessage().equals(item.getMessage()) &&
                                Math.abs(existingItem.getTimestamp().getTime() - item.getTimestamp().getTime()) <= 2000
                );

        if (!isDuplicate) {
            allActivityItems.add(item);
            allActivityItems.sort((item1, item2) ->
                    item2.getTimestamp().compareTo(item1.getTimestamp())
            );

            if (shouldShowItem(item)) {
                new Handler(Looper.getMainLooper()).post(() ->
                        applyFilters(showFood, showGroups, showAchievements, showGoals)
                );
            }
        }
    }

    public void setItems(List<ActivityFeedItem> items) {
        allActivityItems = new ArrayList<>(items);
        allActivityItems.sort((item1, item2) ->
                item2.getTimestamp().compareTo(item1.getTimestamp())
        );
        currentPage = 0;
        applyFilters(showFood, showGroups, showAchievements, showGoals);
    }

    public void applyFilters(boolean showFood, boolean showGroups,
                             boolean showAchievements, boolean showGoals) {
        this.showFood = showFood;
        this.showGroups = showGroups;
        this.showAchievements = showAchievements;
        this.showGoals = showGoals;

        // Create new filtered list
        List<ActivityFeedItem> newFilteredItems = allActivityItems.stream()
                .filter(this::shouldShowItem)
                .collect(Collectors.toList());

        // Sort the filtered items
        newFilteredItems.sort((item1, item2) ->
                item2.getTimestamp().compareTo(item1.getTimestamp())
        );

        filteredActivityItems = newFilteredItems;
        displayedItems.clear();
        currentPage = 0;
        isLoading = false;

        new Handler(Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
            loadMoreItems();
        });
    }

    public void loadMoreItems() {
        if (isLoading) return;
        isLoading = true;

        int startPosition = currentPage * ITEMS_PER_PAGE;
        int endPosition = Math.min(startPosition + ITEMS_PER_PAGE, filteredActivityItems.size());

        if (startPosition < filteredActivityItems.size()) {

            List<ActivityFeedItem> newItems = new ArrayList<>(
                    filteredActivityItems.subList(startPosition, endPosition)
            );

            new Handler(Looper.getMainLooper()).post(() -> {
                int insertPosition = displayedItems.size();
                displayedItems.addAll(newItems);
                notifyItemRangeInserted(insertPosition, newItems.size());
                currentPage++;
                isLoading = false;

                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
            });
        } else {
            isLoading = false;
        }
    }

    public void resetPagination() {
        currentPage = 0;
        displayedItems.clear();
        isLoading = false;
        new Handler(Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
            loadMoreItems();
        });
    }


    private boolean shouldShowItem(ActivityFeedItem item) {
        switch (item.getType()) {
            case FOOD_EATEN:
                return showFood;
            case GROUP_UPDATE:
                return showGroups;
            case ACHIEVEMENT:
                return showAchievements;
            case GOAL_UPDATE:
                return showGoals;
            default:
                return true;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView activityTypeIcon;
        TextView activityTypeLabel;
        TextView timestampText;
        TextView activityMessage;
        TextView activityDetails;
        MaterialCardView detailsCard;
        ImageView activityImage;

        ViewHolder(View itemView) {
            super(itemView);
            activityTypeIcon = itemView.findViewById(R.id.activityTypeIcon);
            activityTypeLabel = itemView.findViewById(R.id.activityTypeLabel);
            timestampText = itemView.findViewById(R.id.timestampText);
            activityMessage = itemView.findViewById(R.id.activityMessage);
            activityDetails = itemView.findViewById(R.id.activityDetails);
            detailsCard = itemView.findViewById(R.id.detailsCard);
            activityImage = itemView.findViewById(R.id.activityImage);
        }

        void bind(ActivityFeedItem item) {
            int iconRes;
            String typeLabel;
            switch (item.getType()) {
                case FOOD_EATEN:
                    iconRes = R.drawable.ic_food;
                    typeLabel = "Food Activity";
                    break;
                case GROUP_UPDATE:
                    iconRes = R.drawable.ic_group;
                    typeLabel = "Group Update";
                    break;
                case ACHIEVEMENT:
                    iconRes = R.drawable.ic_achievement;
                    typeLabel = "Achievement";
                    break;
                case GOAL_UPDATE:
                    iconRes = R.drawable.goals_icon;
                    typeLabel = "Goal Update";
                    break;
                default:
                    iconRes = R.drawable.ic_default;
                    typeLabel = "Activity";
            }

            activityTypeIcon.setImageResource(iconRes);
            activityTypeLabel.setText(typeLabel);
            timestampText.setText(item.getTimestamp().toString());
            activityMessage.setText(item.getMessage());

            if (item.getAdditionalData() != null && !item.getAdditionalData().isEmpty()) {
                activityDetails.setText(item.getAdditionalData());
                detailsCard.setVisibility(View.VISIBLE);
            } else {
                detailsCard.setVisibility(View.GONE);
            }
            if (item.getImages() != null && !item.getImages().isEmpty()) {
                String base64Image = item.getImages().get(0);
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                activityImage.setImageBitmap(decodedByte);
                activityImage.setVisibility(View.VISIBLE);
            } else {
                activityImage.setVisibility(View.GONE);
            }
        }
    }
}
